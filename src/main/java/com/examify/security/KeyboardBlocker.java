package com.examify.security;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Hook keyboard tingkat rendah Windows (WH_KEYBOARD_LL) untuk Mode Aman.
 * Memblokir pintasan sistem yang tidak bisa ditangkap JavaFX: Alt+Tab, tombol Windows,
 * Ctrl+Esc, Alt+Esc, Ctrl+Shift+Esc (Task Manager), Alt+F4, Alt+Spasi, tombol menu, dan Print Screen.
 * Ctrl+Alt+Del tidak bisa diblokir oleh aplikasi mana pun (Secure Attention Sequence Windows).
 */
final class KeyboardBlocker {

    private static final int VK_TAB = 0x09;
    private static final int VK_CONTROL = 0x11;
    private static final int VK_ESCAPE = 0x1B;
    private static final int VK_SPACE = 0x20;
    private static final int VK_SNAPSHOT = 0x2C;
    private static final int VK_LWIN = 0x5B;
    private static final int VK_RWIN = 0x5C;
    private static final int VK_APPS = 0x5D;
    private static final int VK_F4 = 0x73;
    private static final int LLKHF_ALTDOWN = 0x20;
    private static final int TOMBOL_DITEKAN = 0x8000;

    private final CountDownLatch siap = new CountDownLatch(1);
    // referensi kuat: callback yang dikumpulkan GC akan membuat proses crash saat hook dipanggil
    private LowLevelKeyboardProc prosedur;
    private volatile HHOOK hook;
    private volatile int idThread;
    private Thread thread;

    static boolean didukung() {
        return com.sun.jna.Platform.isWindows();
    }

    /** @return true bila hook berhasil dipasang */
    boolean pasang() {
        if (!didukung()) {
            return false;
        }
        thread = new Thread(this::jalankan, "Examify-ModeAman-Keyboard");
        thread.setDaemon(true);
        thread.start();
        try {
            siap.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return hook != null;
    }

    void lepas() {
        int id = idThread;
        if (id != 0) {
            User32.INSTANCE.PostThreadMessage(id, WinUser.WM_QUIT, new WPARAM(0), new LPARAM(0));
        }
    }

    private void jalankan() {
        try {
            idThread = Kernel32.INSTANCE.GetCurrentThreadId();
            prosedur = this::tangani;
            HMODULE modul = Kernel32.INSTANCE.GetModuleHandle(null);
            hook = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, prosedur, modul, 0);
        } catch (Throwable t) {
            System.err.println("[ModeAman] Hook keyboard gagal dipasang: " + t);
        } finally {
            siap.countDown();
        }
        if (hook == null) {
            idThread = 0;
            return;
        }
        // hook LL hanya dipanggil selama thread pemasangnya memompa message loop
        MSG pesan = new MSG();
        while (User32.INSTANCE.GetMessage(pesan, null, 0, 0) > 0) {
            User32.INSTANCE.TranslateMessage(pesan);
            User32.INSTANCE.DispatchMessage(pesan);
        }
        User32.INSTANCE.UnhookWindowsHookEx(hook);
        hook = null;
        idThread = 0;
    }

    private LRESULT tangani(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
        try {
            if (nCode >= 0 && harusDiblokir(info.vkCode, (info.flags & LLKHF_ALTDOWN) != 0)) {
                return new LRESULT(1);
            }
        } catch (Throwable t) {
            // pengecualian tidak boleh lolos ke kode native
        }
        return User32.INSTANCE.CallNextHookEx(hook, nCode, wParam, new LPARAM(Pointer.nativeValue(info.getPointer())));
    }

    private static boolean harusDiblokir(int vk, boolean alt) {
        boolean ctrl = ditekan(VK_CONTROL);
        return switch (vk) {
            case VK_LWIN, VK_RWIN, VK_APPS, VK_SNAPSHOT -> true;
            case VK_TAB, VK_SPACE, VK_F4 -> alt;
            case VK_ESCAPE -> alt || ctrl; // Ctrl+Esc, Alt+Esc, Ctrl+Shift+Esc
            default -> false;
        };
    }

    private static boolean ditekan(int vk) {
        return (User32.INSTANCE.GetAsyncKeyState(vk) & TOMBOL_DITEKAN) != 0;
    }
}

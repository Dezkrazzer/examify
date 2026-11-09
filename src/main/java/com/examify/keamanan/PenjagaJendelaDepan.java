package com.examify.keamanan;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

/**
 * Penjaga jendela depan untuk Mode Aman (khusus Windows).
 *
 * <p>Gesture touchpad (swipe 3/4 jari untuk pindah aplikasi, Task View, atau pindah desktop virtual)
 * ditangani langsung oleh Windows, sehingga tidak lewat hook keyboard dan tidak bisa diblokir.
 * Kelas ini memantau jendela aktif secara native setiap {@value #JEDA_MS} ms; begitu jendela milik
 * proses lain menjadi aktif, jendela ujian langsung direbut kembali ke depan dan pelanggaran dilaporkan.
 *
 * <p>{@code SetForegroundWindow} biasa ditolak Windows bila dipanggil dari proses di latar belakang,
 * maka input thread dihubungkan sementara ke thread jendela aktif ({@code AttachThreadInput}).
 */
final class PenjagaJendelaDepan {

    private static final long JEDA_MS = 150;
    private static final int SW_SHOW = 5;
    private static final int GW_OWNER = 4;

    private final String judulJendela;
    private final Runnable saatDirebut;
    private volatile boolean jalan;
    private volatile HWND jendelaUjian;
    private Thread thread;

    /**
     * @param judulJendela judul stage ujian, dipakai mencari HWND milik proses ini
     * @param saatDirebut  dipanggil (dari thread penjaga) setiap kali aplikasi lain sempat aktif
     */
    PenjagaJendelaDepan(String judulJendela, Runnable saatDirebut) {
        this.judulJendela = judulJendela;
        this.saatDirebut = saatDirebut;
    }

    boolean mulai() {
        if (!PemblokirKeyboard.didukung()) {
            return false;
        }
        jalan = true;
        thread = new Thread(this::pantau, "Examify-ModeAman-JendelaDepan");
        thread.setDaemon(true);
        thread.start();
        return true;
    }

    void berhenti() {
        jalan = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    private void pantau() {
        int pidSendiri = Kernel32.INSTANCE.GetCurrentProcessId();
        while (jalan) {
            try {
                HWND aktif = User32.INSTANCE.GetForegroundWindow();
                if (aktif != null && pidDari(aktif) != pidSendiri) {
                    HWND ujian = cariJendelaUjian(pidSendiri);
                    if (ujian != null) {
                        rebut(aktif, ujian);
                        saatDirebut.run();
                    }
                }
                Thread.sleep(JEDA_MS);
            } catch (InterruptedException e) {
                return;
            } catch (Throwable t) {
                System.err.println("[ModeAman] penjaga jendela: " + t);
                return;
            }
        }
    }

    private static int pidDari(HWND hwnd) {
        IntByReference pid = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);
        return pid.getValue();
    }

    private HWND cariJendelaUjian(int pidSendiri) {
        HWND tersimpan = jendelaUjian;
        if (tersimpan != null && User32.INSTANCE.IsWindow(tersimpan)) {
            return tersimpan;
        }
        HWND[] hasil = new HWND[1];
        User32.INSTANCE.EnumWindows((HWND hwnd, Pointer data) -> {
            if (pidDari(hwnd) == pidSendiri && User32.INSTANCE.IsWindowVisible(hwnd)
                    && User32.INSTANCE.GetWindow(hwnd, new DWORD(GW_OWNER)) == null // bukan dialog
                    && judulJendela.equals(judul(hwnd))) {
                hasil[0] = hwnd;
                return false;
            }
            return true;
        }, null);
        jendelaUjian = hasil[0];
        return hasil[0];
    }

    private static String judul(HWND hwnd) {
        char[] buffer = new char[256];
        int panjang = User32.INSTANCE.GetWindowText(hwnd, buffer, buffer.length);
        return new String(buffer, 0, Math.max(0, panjang));
    }

    private static void rebut(HWND aktif, HWND ujian) {
        User32 u = User32.INSTANCE;
        int threadAktif = u.GetWindowThreadProcessId(aktif, null);
        int threadIni = Kernel32.INSTANCE.GetCurrentThreadId();
        boolean terhubung = threadAktif != threadIni
                && u.AttachThreadInput(new DWORD(threadIni), new DWORD(threadAktif), true);
        try {
            u.ShowWindow(ujian, SW_SHOW);
            u.BringWindowToTop(ujian);
            u.SetForegroundWindow(ujian);
            u.SetFocus(ujian);
        } finally {
            if (terhubung) {
                u.AttachThreadInput(new DWORD(threadIni), new DWORD(threadAktif), false);
            }
        }
    }
}

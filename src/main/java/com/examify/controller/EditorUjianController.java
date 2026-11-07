package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.app.Layar;
import com.examify.app.SceneRouter;
import com.examify.model.JenisSoal;
import com.examify.model.Soal;
import com.examify.model.SoalIsian;
import com.examify.model.SoalPilihanGanda;
import com.examify.model.SoalTrueFalse;
import com.examify.model.Ujian;
import com.examify.utils.FormatUtil;
import com.examify.utils.StringUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditorUjianController {

    private static class DrafSoal {
        int id;
        String pertanyaan;
        JenisSoal jenis;
        double bobot;
        List<String> opsiPG;
        char kunciPG;
        char kunciTF;
        String kunciIS;

        DrafSoal(int id, String pertanyaan, JenisSoal jenis, double bobot) {
            this.id = id;
            this.pertanyaan = pertanyaan;
            this.jenis = jenis;
            this.bobot = bobot;
            this.opsiPG = new ArrayList<>(List.of("Opsi A", "Opsi B", "Opsi C", "Opsi D"));
            this.kunciPG = 'A';
            this.kunciTF = 'T';
            this.kunciIS = "";
        }
    }

    @FXML private Label lblHeaderUjian;
    @FXML private TextField tfMapel;
    @FXML private TextField tfDurasi;
    @FXML private TextField tfKkm;
    @FXML private TextField tfToken;
    @FXML private TextField tfAksesMulai;
    @FXML private TextField tfAksesSelesai;

    @FXML private Label lblJumlahSoal;
    @FXML private ListView<DrafSoal> lvSoal;

    @FXML private Label lblNomorSoal;
    @FXML private TextField tfBobot;
    @FXML private TextArea taPertanyaan;
    @FXML private ComboBox<JenisSoal> cbJenisSoal;
    @FXML private VBox vbAreaKunci;

    private final AppSession session = AppSession.getInstance();
    private final ObservableList<DrafSoal> drafList = FXCollections.observableArrayList();
    private Ujian ujian;
    private DrafSoal drafAktif;
    private boolean sedangMemuatForm = false;

    @FXML
    private void initialize() {
        ujian = session.getUjianAktif();
        if (ujian == null) {
            SceneRouter.pindah(Layar.KURSUS_SAYA);
            return;
        }

        lblHeaderUjian.setText("KODE: " + ujian.getKodeUjian());
        tfMapel.setText(ujian.getMataPelajaran());
        tfDurasi.setText(String.valueOf(ujian.getDurasiMenit()));
        tfKkm.setText(ujian.getKkm() == (long) ujian.getKkm() ? String.format("%d", (long) ujian.getKkm()) : String.valueOf(ujian.getKkm()));
        tfToken.setText(ujian.getToken());
        tfAksesMulai.setText(FormatUtil.formatInputAkses(ujian.getWaktuMulai()));
        tfAksesSelesai.setText(FormatUtil.formatInputAkses(ujian.getWaktuSelesai()));

        cbJenisSoal.getItems().setAll(JenisSoal.values());
        cbJenisSoal.valueProperty().addListener((obs, lama, baru) -> {
            if (!sedangMemuatForm && drafAktif != null && baru != null) {
                drafAktif.jenis = baru;
                tampilkanAreaKunci(drafAktif);
                lvSoal.refresh();
            }
        });

        taPertanyaan.textProperty().addListener((obs, lama, baru) -> {
            if (!sedangMemuatForm && drafAktif != null) {
                drafAktif.pertanyaan = baru;
                lvSoal.refresh();
            }
        });

        tfBobot.textProperty().addListener((obs, lama, baru) -> {
            if (!sedangMemuatForm && drafAktif != null) {
                try {
                    drafAktif.bobot = Double.parseDouble(baru);
                } catch (NumberFormatException ignored) {
                }
            }
        });

        lvSoal.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DrafSoal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int no = getIndex() + 1;
                    VBox card = new VBox(3);

                    HBox barisAtas = new HBox(6);
                    barisAtas.setAlignment(Pos.CENTER_LEFT);

                    Label lblNo = new Label("Soal " + no);
                    lblNo.setStyle("-fx-font-weight: bold;");

                    Label lblBadge = new Label("[" + item.jenis.getKode() + "]");
                    lblBadge.getStyleClass().add("teks-kecil");

                    barisAtas.getChildren().addAll(lblNo, lblBadge);

                    String ringkasan = item.pertanyaan != null ? item.pertanyaan.trim() : "";
                    if (ringkasan.length() > 28) {
                        ringkasan = ringkasan.substring(0, 25) + "...";
                    }
                    Label lblPreview = new Label(ringkasan.isEmpty() ? "(Kosong)" : ringkasan);
                    lblPreview.getStyleClass().add("teks-redup");
                    lblPreview.setStyle("-fx-font-size: 11px;");

                    card.getChildren().addAll(barisAtas, lblPreview);
                    setGraphic(card);
                    setText(null);
                }
            }
        });

        lvSoal.setItems(drafList);
        lvSoal.getSelectionModel().selectedItemProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                pilihSoal(baru);
            }
        });

        muatSoalDariUjian();
    }

    private void updateJumlahSoalHeader() {
        lblJumlahSoal.setText("Daftar Soal (" + drafList.size() + ")");
    }

    private void muatSoalDariUjian() {
        drafList.clear();
        for (Soal s : ujian.getDaftarSoal()) {
            DrafSoal d = new DrafSoal(s.getIdSoal(), s.getPertanyaan(), s.getJenis(), s.getBobot());
            if (s instanceof SoalPilihanGanda pg) {
                d.opsiPG = new ArrayList<>(pg.getOpsi());
                d.kunciPG = pg.getKunciRaw();
            } else if (s instanceof SoalTrueFalse tf) {
                d.kunciTF = tf.getKunciRaw();
            } else if (s instanceof SoalIsian is) {
                d.kunciIS = String.join(";", is.getKunciJawaban());
            }
            drafList.add(d);
        }

        updateJumlahSoalHeader();

        if (!drafList.isEmpty()) {
            lvSoal.getSelectionModel().select(0);
        }
    }

    private void pilihSoal(DrafSoal draf) {
        drafAktif = draf;
        sedangMemuatForm = true;

        int nomor = drafList.indexOf(draf) + 1;
        lblNomorSoal.setText("Edit Soal #" + nomor + " (ID: " + draf.id + ")");
        taPertanyaan.setText(draf.pertanyaan);
        tfBobot.setText(draf.bobot == (long) draf.bobot ? String.valueOf((long) draf.bobot) : String.valueOf(draf.bobot));
        cbJenisSoal.setValue(draf.jenis);

        tampilkanAreaKunci(draf);
        sedangMemuatForm = false;
    }

    private void tampilkanAreaKunci(DrafSoal draf) {
        vbAreaKunci.getChildren().clear();

        if (draf.jenis == JenisSoal.PG) {
            Label lblOpsi = new Label("Pilihan Jawaban (Opsi):");
            lblOpsi.getStyleClass().add("teks-kecil");
            vbAreaKunci.getChildren().add(lblOpsi);

            char huruf = 'A';
            for (int i = 0; i < draf.opsiPG.size(); i++) {
                final int idx = i;
                char h = (char) (huruf + i);
                HBox baris = new HBox(8);
                baris.setAlignment(Pos.CENTER_LEFT);

                Label lblHuruf = new Label(h + ".");
                lblHuruf.setMinWidth(20);
                lblHuruf.setStyle("-fx-font-weight: bold;");

                TextField tf = new TextField(draf.opsiPG.get(i));
                tf.setPromptText("Teks pilihan " + h);
                HBox.setHgrow(tf, Priority.ALWAYS);
                tf.textProperty().addListener((obs, lama, baru) -> {
                    if (!sedangMemuatForm) {
                        draf.opsiPG.set(idx, baru);
                    }
                });

                baris.getChildren().addAll(lblHuruf, tf);

                if (draf.opsiPG.size() > 2) {
                    Button btnHapusOpsi = new Button("✕");
                    btnHapusOpsi.getStyleClass().add("tombol-hantu");
                    btnHapusOpsi.setStyle("-fx-padding: 2 8; -fx-text-fill: -teks-redup;");
                    btnHapusOpsi.setOnAction(e -> {
                        draf.opsiPG.remove(idx);
                        char maxKunci = (char) ('A' + draf.opsiPG.size() - 1);
                        if (draf.kunciPG > maxKunci) {
                            draf.kunciPG = 'A';
                        }
                        tampilkanAreaKunci(draf);
                    });
                    baris.getChildren().add(btnHapusOpsi);
                }

                vbAreaKunci.getChildren().add(baris);
            }

            if (draf.opsiPG.size() < 5) {
                Button btnTambahOpsi = new Button("+ Tambah Pilihan Jawaban");
                btnTambahOpsi.getStyleClass().add("tombol-sekunder");
                btnTambahOpsi.setOnAction(e -> {
                    char h = (char) ('A' + draf.opsiPG.size());
                    draf.opsiPG.add("Opsi " + h);
                    tampilkanAreaKunci(draf);
                });
                vbAreaKunci.getChildren().add(btnTambahOpsi);
            }

            HBox barisKunci = new HBox(10);
            barisKunci.setAlignment(Pos.CENTER_LEFT);
            Label lblKunci = new Label("Kunci Jawaban yang Benar:");
            lblKunci.getStyleClass().add("teks-kecil");

            ComboBox<Character> cbKunci = new ComboBox<>();
            for (int i = 0; i < draf.opsiPG.size(); i++) {
                cbKunci.getItems().add((char) ('A' + i));
            }
            if (!cbKunci.getItems().contains(draf.kunciPG)) {
                draf.kunciPG = 'A';
            }
            cbKunci.setValue(draf.kunciPG);
            cbKunci.valueProperty().addListener((obs, lama, baru) -> {
                if (!sedangMemuatForm && baru != null) {
                    draf.kunciPG = baru;
                }
            });

            barisKunci.getChildren().addAll(lblKunci, cbKunci);
            vbAreaKunci.getChildren().add(barisKunci);

        } else if (draf.jenis == JenisSoal.TF) {
            Label lblKunci = new Label("Kunci Pernyataan (Benar atau Salah):");
            lblKunci.getStyleClass().add("teks-kecil");

            ComboBox<String> cbTF = new ComboBox<>();
            cbTF.getItems().addAll("T - Benar (True)", "F - Salah (False)");
            cbTF.setValue(draf.kunciTF == 'T' ? "T - Benar (True)" : "F - Salah (False)");
            cbTF.valueProperty().addListener((obs, lama, baru) -> {
                if (!sedangMemuatForm && baru != null) {
                    draf.kunciTF = baru.startsWith("T") ? 'T' : 'F';
                }
            });

            vbAreaKunci.getChildren().addAll(lblKunci, cbTF);

        } else if (draf.jenis == JenisSoal.IS) {
            Label lblKunci = new Label("Kunci Jawaban Singkat (pisahkan dengan ';' jika ada variasi jawaban sah):");
            lblKunci.getStyleClass().add("teks-kecil");

            TextField tfIsian = new TextField(draf.kunciIS);
            tfIsian.setPromptText("Contoh: new;keyword new");
            tfIsian.textProperty().addListener((obs, lama, baru) -> {
                if (!sedangMemuatForm) {
                    draf.kunciIS = baru;
                }
            });

            vbAreaKunci.getChildren().addAll(lblKunci, tfIsian);
        }
    }

    @FXML
    private void handleTambahSoal() {
        int maxId = drafList.stream().mapToInt(d -> d.id).max().orElse(0);
        int noBaru = drafList.size() + 1;
        DrafSoal baru = new DrafSoal(maxId + 1, "Pertanyaan baru " + noBaru + "...", JenisSoal.PG, 10.0);
        drafList.add(baru);
        updateJumlahSoalHeader();
        lvSoal.getSelectionModel().select(baru);
        lvSoal.scrollTo(baru);
        taPertanyaan.requestFocus();
        taPertanyaan.selectAll();
    }

    @FXML
    private void handleHapusSoal() {
        if (drafList.size() <= 1) {
            DialogUtil.peringatan("Ujian minimal harus memiliki 1 soal.");
            return;
        }
        if (drafAktif == null) return;

        if (DialogUtil.konfirmasi("Hapus soal #" + (drafList.indexOf(drafAktif) + 1) + " dari ujian?")) {
            int idx = drafList.indexOf(drafAktif);
            drafList.remove(drafAktif);
            updateJumlahSoalHeader();
            int pilihBaru = Math.min(idx, drafList.size() - 1);
            lvSoal.getSelectionModel().select(pilihBaru);
            lvSoal.scrollTo(pilihBaru);
        }
    }

    @FXML
    private void handleSimpan() {
        try {
            String mapel = tfMapel.getText().trim();
            if (StringUtil.isKosong(mapel)) {
                throw new IllegalArgumentException("Mata pelajaran / judul ujian wajib diisi");
            }

            int durasi;
            try {
                durasi = Integer.parseInt(tfDurasi.getText().trim());
                if (durasi < Ujian.DURASI_MIN_MENIT || durasi > Ujian.DURASI_MAKS_MENIT) {
                    throw new IllegalArgumentException("Durasi harus antara 1-180 menit");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Durasi harus berupa angka (menit)");
            }

            double kkm;
            try {
                kkm = Double.parseDouble(tfKkm.getText().trim());
                if (kkm < 0 || kkm > 100) {
                    throw new IllegalArgumentException("KKM harus antara 0-100");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("KKM harus berupa angka");
            }

            String token = tfToken.getText().trim().toUpperCase();
            if (token.length() != 6 || !token.matches("^[A-Z0-9]{6}$")) {
                throw new IllegalArgumentException("Token ujian harus terdiri dari 6 karakter huruf/angka");
            }

            if (drafList.isEmpty()) {
                throw new IllegalArgumentException("Ujian minimal harus memiliki 1 soal");
            }

            List<Soal> hasilSoal = new ArrayList<>();
            for (int i = 0; i < drafList.size(); i++) {
                DrafSoal d = drafList.get(i);
                int nomorUrut = i + 1;
                if (StringUtil.isKosong(d.pertanyaan)) {
                    throw new IllegalArgumentException("Pertanyaan pada Soal #" + nomorUrut + " tidak boleh kosong");
                }
                if (d.bobot <= 0) {
                    throw new IllegalArgumentException("Bobot pada Soal #" + nomorUrut + " harus lebih besar dari 0");
                }

                if (d.jenis == JenisSoal.PG) {
                    List<String> opsiBersih = d.opsiPG.stream().map(String::trim).toList();
                    for (String opt : opsiBersih) {
                        if (StringUtil.isKosong(opt)) {
                            throw new IllegalArgumentException("Semua opsi pilihan ganda pada Soal #" + nomorUrut + " wajib diisi");
                        }
                    }
                    hasilSoal.add(new SoalPilihanGanda(d.id, d.pertanyaan.trim(), d.bobot, opsiBersih, d.kunciPG));
                } else if (d.jenis == JenisSoal.TF) {
                    hasilSoal.add(new SoalTrueFalse(d.id, d.pertanyaan.trim(), d.bobot, d.kunciTF));
                } else if (d.jenis == JenisSoal.IS) {
                    if (StringUtil.isKosong(d.kunciIS)) {
                        throw new IllegalArgumentException("Kunci jawaban isian pada Soal #" + nomorUrut + " wajib diisi");
                    }
                    String[] pecah = d.kunciIS.split(";");
                    Set<String> setKunci = new HashSet<>();
                    for (String k : pecah) {
                        if (!StringUtil.isKosong(k)) {
                            setKunci.add(k.trim());
                        }
                    }
                    if (setKunci.isEmpty()) {
                        throw new IllegalArgumentException("Kunci jawaban isian pada Soal #" + nomorUrut + " tidak boleh kosong");
                    }
                    hasilSoal.add(new SoalIsian(d.id, d.pertanyaan.trim(), d.bobot, setKunci));
                }
            }

            LocalDateTime waktuMulai = FormatUtil.parseInputAkses(tfAksesMulai != null ? tfAksesMulai.getText() : null);
            LocalDateTime waktuSelesai = FormatUtil.parseInputAkses(tfAksesSelesai != null ? tfAksesSelesai.getText() : null);
            if (waktuMulai != null && waktuSelesai != null && waktuMulai.isAfter(waktuSelesai)) {
                throw new IllegalArgumentException("Waktu mulai akses tidak boleh lebih lambat dari waktu tutup akses");
            }

            Ujian ujianBaru = new Ujian(ujian.getKodeUjian(), mapel, token, durasi, kkm, hasilSoal, ujian.isBolehUlang(), waktuMulai, waktuSelesai);

            session.getUjianService().perbaruiUjian(ujianBaru);
            session.setUjianAktif(ujianBaru);
            DialogUtil.info("Perubahan ujian " + ujianBaru.getKodeUjian() + " (" + hasilSoal.size() + " soal) berhasil disimpan!");
            kembaliKeLayarAsal();

        } catch (IllegalArgumentException e) {
            DialogUtil.peringatan("Validasi gagal: " + e.getMessage());
        } catch (IOException e) {
            DialogUtil.peringatan("Gagal menyimpan ke file bank_soal.txt: " + e.getMessage());
        }
    }

    @FXML
    private void handleKembali() {
        kembaliKeLayarAsal();
    }

    private void kembaliKeLayarAsal() {
        if (session.getKursusAktif() != null) {
            SceneRouter.pindah(Layar.KURSUS_DETAIL);
        } else {
            SceneRouter.pindah(Layar.BANK_SOAL);
        }
    }
}

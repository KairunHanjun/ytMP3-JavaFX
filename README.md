# ytMP3-JavaFX

**Eksplorasi aplikasi desktop Java: struktur data dan pemrosesan media.**

[English](README.en.md) · [Kode sumber](https://github.com/KairunHanjun/ytMP3-JavaFX) · [Apache 2.0](LICENSE)

Proyek ini menggabungkan latihan **binary search tree** dengan antarmuka pengunduh media melalui **yt-dlp** dan **FFmpeg**. Tujuannya mempelajari logika data, event handling desktop, dan komunikasi dengan proses eksternal menggunakan JavaFX.

Layar awal bernama **BINARY TREE**. Tombol **Go to Youtube Downloader** membuka modul pengunduh. Layout FXML asli dipertahankan pada penyempurnaan demo lokal.

## Fitur

| Modul | Kemampuan |
| --- | --- |
| Binary search tree | Menambah nilai, menampilkan struktur, dan mencari nilai. |
| Pengunduh | URL media, folder tujuan, pilihan MP3/MP4, dan konfigurasi pekerjaan. |
| Konfigurasi | Pengaturan format, kualitas, dan thread melalui dialog serta file JSON. |
| Penyempurnaan lokal | Background worker, progres, pembatalan proses, dan dukungan path ber-spasi. |

Opsi playlist tersedia pada antarmuka; kompatibilitas ekstraksi YouTube daring terbaru belum diverifikasi.

## Teknologi dan kebutuhan

- Java, JavaFX/FXML, Maven, serta Jackson untuk konfigurasi JSON.
- yt-dlp untuk ekstraksi/unduhan; FFmpeg untuk pemrosesan media.
- Demo lokal diverifikasi menggunakan **JDK 21**, Maven 3.9+, Python 3, yt-dlp, dan FFmpeg.
- JavaFX pada konfigurasi proyek masih **20-ea+11**.

## Menjalankan source repository

```sh
git clone https://github.com/KairunHanjun/ytMP3-JavaFX.git
cd ytMP3-JavaFX
mvn compile javafx:run
```

Siapkan JDK, Maven, yt-dlp, serta FFmpeg terlebih dahulu. Periksa nama/path executable pada source awal sesuai instalasi Anda. Konfigurasi packaging lama mengandung path khusus komputer pengembang; build EXE tidak dianggap portabel atau terverifikasi oleh panduan ini.

## Demo portofolio lokal

> Demo berikut mengacu pada penyempurnaan di workspace portofolio. Commit README/LICENSE ini tidak menyertakan perubahan implementasi yang masih lokal. Periksa keberadaan `start-demo.ps1`, source perbaikan, dan tes terkait sebelum menggunakan perintah ini.

```powershell
.\start-demo.ps1
```

Launcher memilih JDK 21/Maven portable pada `../.portfolio-tools` bila tersedia. Pada komputer lain, atur `JAVA_HOME`, siapkan Maven di PATH, serta pastikan yt-dlp, FFmpeg, dan Python tersedia. Tidak ada instalasi software otomatis.

Demo membuat video sintetis **20 detik** berupa pola warna dan nada, lalu menyajikannya melalui loopback. Tidak membutuhkan database, akun, atau media dari pihak lain.

1. Buka **Go to Youtube Downloader**.
2. Link dan folder demo terisi otomatis; klik **Download** untuk MP4.
3. Untuk MP3, buka **Setting**, nonaktifkan MP4 agar MP3 terpilih, lalu **Apply** dan unduh kembali.

| Berkas | Lokasi demo |
| --- | --- |
| Media sintetis | `.demo/fixture/Portfolio-Demo.mp4` |
| Hasil unduhan | `.demo/output/` |
| Konfigurasi terpisah | `.demo/setting.json` |

URL lokal hanya diterima ketika mode demo aktif. Mode biasa menggunakan `setting.json` dan URL YouTube.

## Pemeriksaan dan build lokal

Dengan source demo yang sesuai:

```powershell
.\start-demo.ps1 -TestOnly
.\start-demo.ps1 -BuildOnly
```

Verifikasi workspace pada 16 Oktober 2026 mencatat **21 tes lulus**: 17 tes inti dan 4 integrasi nyata. Pengujian mencakup argumen proses, path ber-spasi, validasi URL, progres UTF-8, BST, settings, unduhan MP4, dan ekstraksi MP3. Media sintetis diperiksa menggunakan FFprobe.

`-BuildOnly` menghasilkan JAR dan melewati tes; tes dijalankan terpisah melalui `-TestOnly`. JAR memerlukan dependensi JavaFX dan bukan installer mandiri. Hasil tes/CLI tersebut tidak diklaim sebagai bukti klik UI atau hasil CI source awal repository.

## Struktur ringkas

```text
src/main/java/com/iseng/binarytree/         Aplikasi dan controller JavaFX
src/main/java/com/iseng/binarytree/youtube/ Integrasi proses media
src/main/resources/                       Layout FXML
pom.xml                                   Dependency dan konfigurasi Maven
```

## Orisinalitas dan batas

Source awal commit `ac64a55` diarsipkan pada workspace lokal sebelum penyempurnaan. Perbaikan lokal dibantu Codex dan berfokus pada logika serta proses background; FXML tidak didesain ulang. Proyek awal dikembangkan mandiri oleh **Guntur Ahmad Lion Putranto**.

Dokumentasi UI yang tersedia belum membuktikan pengujian klik terbaru selesai. Playlist daring, fitur akun lama, pembaruan dependensi, dan distribusi native masih menjadi pekerjaan berikutnya. EXE/runtime image lama belum dibangun ulang.

## Lisensi

Kode proyek dilisensikan dengan **Apache License 2.0**; lihat [LICENSE](LICENSE). Dependensi dan alat eksternal seperti yt-dlp/FFmpeg tetap mengikuti lisensinya masing-masing.

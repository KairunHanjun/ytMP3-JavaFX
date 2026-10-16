# ytMP3-JavaFX

**Exploring Java desktop development through data structures and media processing.**

[Bahasa Indonesia](README.md) · [Source](https://github.com/KairunHanjun/ytMP3-JavaFX) · [Apache 2.0](LICENSE)

This project combines a **binary search tree** exercise with a media downloader interface using **yt-dlp** and **FFmpeg**. It explores data structure logic, desktop events, and communication with external processes through JavaFX.

The initial screen is **BINARY TREE**. Click **Go to Youtube Downloader** to open the media module. Original FXML layouts are retained in the refined local demo.

## Features

| Module | Capabilities |
| --- | --- |
| Binary search tree | Insert values, display the structure, and search. |
| Downloader | Media URL, destination folder, MP3/MP4 selection, job configuration. |
| Settings | Format, quality, and thread options with JSON persistence. |
| Local refinements | Background workers, progress, process cancellation, and whitespace paths. |

Playlist options exist in the interface; current online YouTube extraction compatibility remains unverified.

## Stack and requirements

Java, JavaFX/FXML, Maven, Jackson, yt-dlp, and FFmpeg. The local demo was checked with **JDK 21**, Maven 3.9+, Python 3, yt-dlp, and FFmpeg. The project's JavaFX configuration remains **20-ea+11**.

## Run the repository source

```sh
git clone https://github.com/KairunHanjun/ytMP3-JavaFX.git
cd ytMP3-JavaFX
mvn compile javafx:run
```

Install the required runtimes and review the executable name/path in the original source. Legacy packaging contains a developer-machine-specific path; this guide does not establish a portable or verified EXE build.

## Local portfolio demo

> These steps refer to refinements in the portfolio workspace. This README/LICENSE update does not include implementation changes that remain local. Confirm the launcher, refined source, and related tests are present before using these commands.

```powershell
.\start-demo.ps1
```

The launcher uses sibling portable JDK 21/Maven tools when available. Elsewhere, set `JAVA_HOME`, provide Maven on PATH, and install yt-dlp, FFmpeg, and Python. It does not install software automatically.

The demo generates a **20-second** synthetic color-pattern video with a tone and serves it over loopback. No database, account, or third-party media is required.

1. Open **Go to Youtube Downloader**.
2. The demo URL and folder are prefilled; click **Download** for MP4.
3. For MP3, open **Setting**, deselect MP4 to select MP3, then **Apply** and download again.

| File | Demo location |
| --- | --- |
| Synthetic media | `.demo/fixture/Portfolio-Demo.mp4` |
| Output | `.demo/output/` |
| Isolated settings | `.demo/setting.json` |

Local URLs require demo mode. Normal mode uses `setting.json` and YouTube URLs.

## Local checks and build

With the matching demo source:

```powershell
.\start-demo.ps1 -TestOnly
.\start-demo.ps1 -BuildOnly
```

Workspace verification on 16 October 2026 recorded **21 passing tests**: 17 core tests and 4 real integration tests. Coverage includes process arguments, whitespace paths, URL validation, UTF-8 progress, BST logic, settings, MP4 downloading, and MP3 extraction. FFprobe checked synthetic output.

BuildOnly produces a JAR and skips tests; TestOnly runs them separately. The JAR requires JavaFX dependencies and is not a standalone installer. Test/CLI results are not presented as UI click-through evidence or CI results for the original repository source.

## Structure

```text
src/main/java/com/iseng/binarytree/         Application and JavaFX controllers
src/main/java/com/iseng/binarytree/youtube/ External media process integration
src/main/resources/                       FXML layouts
pom.xml                                   Maven dependencies and configuration
```

## Preservation and limitations

Original source at commit `ac64a55` was archived locally before refinements. Codex assisted with internal logic and background processing; FXML was not redesigned. The original project was independently developed by **Guntur Ahmad Lion Putranto**.

Available UI documentation does not establish completed current click-through testing. Online playlists, legacy account controls, dependency modernization, and native distribution remain future work. The legacy EXE/runtime image is not rebuilt.

## License

Project code is licensed under the **Apache License 2.0**; see [LICENSE](LICENSE). Third-party dependencies and external tools, including yt-dlp and FFmpeg, retain their respective licenses.

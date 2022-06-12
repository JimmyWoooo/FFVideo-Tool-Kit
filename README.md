# FFVideo Tool-Kit
A simple video tool kit used for trimming and merging videos. Built using JavaFX, FFMPEG and FFProbe for video info. You need FFMPEG and FFProbe installed as an environment variable for this to work.

## Features
* **Trimming:** Basic video trimming used to cut out a portion of a video with options to scale between 480p, 720p and 1080p and increase/decrease volume.

* **Cropping:** Basic video cropping used to crop out a specific area of a video.

* **Merging:** Combines all files listed into one. Since drag and drop sometimes unorders the files, you can drag to swap or drag to insert them back in order. **ALL CODECS ARE COPIED.**

* **Overlay:** Places an image or GIF on one of the 4 corners of the video with the ability to rotate it or scale it. 

* **Decimate:** Removes duplicated frames and audio. Supports m3u8 files and links.

* **M3U8 Download:** Downloads m3u8 file or link with the option to encode it using H.264 + AAC while downloading or just copy everything. Can also choose a timestamp to download from and to and increase/decrease volume.

* **Video Codecs:** Copy original, H.264, H.265, MPEG-4 and Google's VP9.

* **Audio Codecs:** Copy original, AAC, FLAC, MP3, OPUS and no audio.

* **Output:** Can either choose your directory for the new video or same directory as the original file. File output format can either be MP4, AVI, MKV, MOV, TS or WebM. **Most of these were not tested so they may or may not work.**

* **Video Info:** Displays basic video info.

* **Output Video Info:** Displays the current progress of the program such as: how much has been written so far, speed, elapsed time, etc.

* **Progress Bar:** Displays progress percentage with the ability to cancel the current process. Can't pause because Java.

# Sample Preview
Took a video created using Sony Vegas and re-encoded it for a smaller file size. **File size shrunk by 99%!!**

<p align="center"><img width="700" src="https://i.imgur.com/wEWynKk.png"></p>

[Youtube Sample](https://www.youtube.com/watch?v=TXBnSP6tHzg)

# Notes
* Not every edge case is covered so there will be bugs here and there.
* This program was made mainly for personal use.
* It's ugly but it works 👍.

# Credits
* [FFMPEG + FFprobe](https://www.ffmpeg.org/)
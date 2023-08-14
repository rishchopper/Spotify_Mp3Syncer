@echo off

Rem Syntax: ./liked_song_downloader.bat {song_url}

title Downloading Liked Songs
echo Download Location: D:\Music\Spotify_Synced

cd /d "D:\Music\Spotify_Synced"

if exist Liked_Songs_temp\ (
  cd Liked_Songs_temp

  spotdl %~1

) else (
  mkdir Liked_Songs_temp
  cd Liked_Songs_temp

  spotdl %~1

)

dir /b /a-d "D:\Music\Spotify_Synced\Liked_Songs_temp\*.mp3" > "Spotify-Liked Songs.m3u8"
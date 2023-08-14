@echo off

Rem Syntax: ./liked_songs_transfer.bat

cd /d "D:\Music\Spotify_Synced"

echo No|copy "D:\Music\Spotify_Synced\Liked_Songs_temp\*.mp3"
echo Yes|copy "D:\Music\Spotify_Synced\Liked_Songs_temp\*.m3u8"

del Liked_Songs_temp /Q
rmdir Liked_Songs_temp
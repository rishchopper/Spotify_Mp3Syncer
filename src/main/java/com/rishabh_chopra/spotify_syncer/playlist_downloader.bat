@echo off

Rem Syntax: ./playlist_downloader.bat {playlist_url} {m3u_name} {auth_token}

title Downloading Songs
echo ======================================================================
echo Download Location: D:\Music\Spotify_Synced

cd /d "D:\Music\Spotify_Synced"

echo spotdl %~1 --m3u %2 --user-auth --auth-token %~3

spotdl %~1 --m3u %2 --user-auth --auth-token %~3
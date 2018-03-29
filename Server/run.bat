@ECHO off
SET INF3995_THIRD_PARTY_DIR=%~dp0%third_party
SET INF3995_SRC_DIR=%~dp0%src
SET PYTHONPATH=%INF3995_THIRD_PARTY_DIR%;%INF3995_SRC_DIR%
CD %~dp0\tools\scripts
CALL py %~dp0\tools\scripts\generateEnums.py > nul
CD %~dp0\working_dir
CALL py %~dp0\src\main.py %*

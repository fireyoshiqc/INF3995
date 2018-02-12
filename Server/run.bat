@ECHO off
SET PYTHONPATH=%~dp0\third_party
CD %~dp0\working_dir
CALL py %~dp0\src\main.py %*

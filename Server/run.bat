@ECHO off
SET PYTHONPATH=%~dp0\third_party
CD %~dp0\working_dir
START CALL py %~dp0\src\main.py 

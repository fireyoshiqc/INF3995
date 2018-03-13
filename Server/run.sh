#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"

export PYTHONPATH=$SCRIPTPATH/third_party:$SCRIPTPATH/src
cd $SCRIPTPATH/tools/scripts
python3 $SCRIPTPATH/tools/scripts/generateEnums.py
cd $SCRIPTPATH/working_dir
python3 $SCRIPTPATH/src/main.py


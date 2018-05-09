#!/bin/bash

#
# The Ant zip task is not capable of preserving file permissions. File 
# permissions in the War File Distribution need to be correct so that 
# linux users will not get permission errors when executing scripts.
#
# This script will use the zip utility native to the linux 
# (presumably) operating system.
#

echo "command to execute: zip -r $1 $2 "

zip -r $1 $2

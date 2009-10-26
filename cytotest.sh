#!/bin/sh
cd `dirname $0`
CWD=`pwd`

cd ~/opt/cytoscape
./cytoscape.sh $CWD/test.cys

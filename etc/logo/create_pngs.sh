#!/bin/bash

if [ -f /usr/bin/inkscape ]; then
	# For Linux systems
	INKSCAPE=/usr/bin/inkscape
else
	# For OS X
	INKSCAPE=/Applications/Inkscape.app/Contents/Resources/bin/inkscape
fi

if [ ! -d png ]; then
	mkdir png
fi

for what in cl1; do
	for width in 270; do
		height=$(($width * 270 / 867))
		${INKSCAPE} -e png/${what}_${width}x${height}.png -w ${width} -h ${height} svg/${what}.svg
	done
done

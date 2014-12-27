#!/bin/bash

pushd "`dirname $0`" >/dev/null
SCRIPT_DIR="`pwd`"
cd ..
ROOT_DIR="`pwd`"
popd >/dev/null

REFERENCE_IMPL=cluster_one-1.0.jar
TESTED_IMPL=cluster_one-1.1.jar

###########################################################################

OUT_DIR="${SCRIPT_DIR}/comparison_results"
rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"

for DATASET_FILE in $(ls "${ROOT_DIR}"/data/*.txt | sort); do
	DATASET_NAME="`basename \"$DATASET_FILE\" .txt`"

	COMMAND="java -jar \"${ROOT_DIR}/${REFERENCE_IMPL}\" \"${DATASET_FILE}\" >\"${OUT_DIR}/reference_results.txt\" 2>/dev/null"
	REFERENCE_TIME=`command time -p sh -c "$COMMAND" 2>&1 | head -1 | awk '{ print $2 }'`

	COMMAND="java -jar \"${ROOT_DIR}/${TESTED_IMPL}\" \"${DATASET_FILE}\" >\"${OUT_DIR}/test_results.txt\" 2>/dev/null"
	TESTED_TIME=`command time -p sh -c "$COMMAND" 2>&1 | head -1 | awk '{ print $2 }'`
	RATIO="`echo \"scale=3; ${REFERENCE_TIME}/${TESTED_TIME}\" | bc -l`"

	if diff -q ${OUT_DIR}/reference_results.txt ${OUT_DIR}/test_results.txt >/dev/null; then
		true
	else
		diff ${OUT_DIR}/reference_results.txt ${OUT_DIR}/test_results.txt >${OUT_DIR}/${DAASET_NAME}.diff
		RATIO="ERROR"
	fi

	echo -e "$DATASET_NAME\t$REFERENCE_TIME\t$TESTED_TIME\t$RATIO"
	rm -f ${OUT_DIR}/reference_results.txt ${OUT_DIR}/test_results.txt
done
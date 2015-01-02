#!/bin/bash

pushd "`dirname $0`" >/dev/null
SCRIPT_DIR="`pwd`"
cd ..
ROOT_DIR="`pwd`"
popd >/dev/null

REFERENCE_IMPL=cluster_one-1.0.jar
TESTED_IMPL=cluster_one-1.1.jar
THREAD_COUNTS="1 2 3 4 5 6 7 8"

###########################################################################

OUT_DIR="${SCRIPT_DIR}/comparison_results"
rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"

for DATASET_FILE in $(ls "${ROOT_DIR}"/data/*.txt | sort); do
	DATASET_NAME="`basename \"$DATASET_FILE\" .txt`"

	COMMAND="java -jar \"${ROOT_DIR}/${REFERENCE_IMPL}\" \"${DATASET_FILE}\" >\"${OUT_DIR}/reference_results.txt\" 2>/dev/null"
	REFERENCE_TIME=`command time -p sh -c "$COMMAND" 2>&1 | head -1 | awk '{ print $2 }'`

	TESTED_TIMES=""
	MESSAGE="OK"

	for NUM_THREADS in $THREAD_COUNTS; do
		COMMAND="java -jar \"${ROOT_DIR}/${TESTED_IMPL}\" \"${DATASET_FILE}\" --num-threads ${NUM_THREADS} >\"${OUT_DIR}/test_results.txt\" 2>/dev/null"
		TESTED_TIME=`command time -p sh -c "$COMMAND" 2>&1 | head -1 | awk '{ print $2 }'`
		TESTED_TIMES="$TESTED_TIMES\t$TESTED_TIME"

		if diff -q ${OUT_DIR}/reference_results.txt ${OUT_DIR}/test_results.txt >/dev/null; then
			true
		else
			diff ${OUT_DIR}/reference_results.txt ${OUT_DIR}/test_results.txt >${OUT_DIR}/${DATASET_NAME}_threads=${NUM_THREADS}.diff
			MESSAGE="ERROR"
		fi
	done

	echo -e "$DATASET_NAME\t$MESSAGE\t$REFERENCE_TIME$TESTED_TIMES"
	rm -f ${OUT_DIR}/reference_results.txt ${OUT_DIR}/test_results.txt
done
#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Only pass the amount of commits and one translation file!"
    exit 1
fi

DIFF=$(git diff -U0 HEAD~$1 ${@:2} | grep -E "^\+" | grep -v +++ | cut -c 2- | sed 's/^[ \t]*\(.*$\)/\1/')
echo "<xml>$DIFF</xml>" | xmlstarlet sel -t -m '//string' -v . -n > changed_texts.txt
TRANSLATIONS=$(cat changed_texts.txt)
TRANSLATIONS="${TRANSLATIONS//'%'/' ; '}"
TRANSLATIONS="${TRANSLATIONS//$'\n'/' ; '}"
TRANSLATIONS="${TRANSLATIONS//$'\r'/' ; '}"
echo $TRANSLATIONS
#!/bin/bash
set -ex

# Build script for Travis-CI.
SCRIPTDIR=$(cd $(dirname "$0") && pwd)
ROOTDIR="$SCRIPTDIR/../.."
HOMEDIR="$ROOTDIR/../"
WHISKDIR="$HOMEDIR/openwhisk"

export OPENWHISK_HOME=${OPENWHISK_HOME:=$WHISKDIR}

cd ${HOMEDIR}

# shallow clone OpenWhisk repo.
git clone --depth 1 https://github.com/apache/openwhisk.git ${OPENWHISK_HOME}

# shallow clone deploy package repo.
git clone --depth 1 https://github.com/apache/openwhisk-package-deploy

# shallow clone of scancode
git clone --depth 1 https://github.com/apache/openwhisk-utilities.git

# use runtimes.json that defines ibm runtimes
cp -f ${ROOTDIR}/ansible/files/runtimes.json ${WHISKDIR}/ansible/files/runtimes.json

cd ${OPENWHISK_HOME}
./tools/travis/setup.sh

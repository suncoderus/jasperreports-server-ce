#!/bin/bash

#
# JasperReports Server common installation and upgrade script.
#
# Usage: do-js-setup.sh (install|upgrade){setup mode} (ce|pro){edition} {script option} {Ant target} [Ant options]
#

# -----------------------------------------------------------------------------

fail() {
  if [[ -n $1 ]]; then
    echo $1
  fi
  showUsage
  exit 1;
}

showUsage() {
  echo "Usage: ./do-js-setup.sh (install|upgrade){setup mode} (ce|pro){edition} {script option} {Ant target} [Ant options]"
}

#
# Selects bundled or existing Ant.
#
initializeAntEnvironment() {
  if test -d ../apache-ant
  then
    ANT_HOME=../apache-ant
    export ANT_HOME

    ANT_RUN=$ANT_HOME/bin/ant
    export ANT_RUN

    PATH=$ANT_HOME/bin:$PATH
    export PATH
  else
    log "Bundled Ant not found. Using existing Ant."

    ANT_RUN=ant
  fi
}

#
# Runs Ant.
#
runAnt() {
  targetName=$1
  shift
  options=$@

  if [ "$targetName" != "" ]; then
    log "Running $targetName Ant task."
    log

    $ANT_RUN --noconfig -nouserlib -f build.xml $targetName $options 2>&1 | tee -a $JS_LOG_FILE
    antReturnCode=$?

    antReturnCodeMessage="Checking Ant return code:"
    if [ $antReturnCode -ne 0 ]; then
      log "$antReturnCodeMessage BAD ($antReturnCode)"
      exit 1;
    else
      log "$antReturnCodeMessage OK"
    fi

  else
    log "Name of Ant target not specified."
    exit 1;
  fi
}

#
# Console + file logging subroutine.
#
log() {
  if [ "$1" == "" ]; then
    JS_OUT_STR="----------------------------------------------------------------------"
  else
    JS_OUT_STR=$1
  fi
  echo $JS_OUT_STR | tee -a $JS_LOG_FILE
}

# -----------------------------------------------------------------------------

#
# Validating arguments and setting internal variables.
#

if [[ $# -lt 4 || $# -gt 8 ]]; then
  fail "Invalid argument count"
fi

JS_SETUP_MODE=$1
if [[ "$JS_SETUP_MODE" != "install" && "$JS_SETUP_MODE" != "upgrade" ]]; then
  fail "Setup mode (install|upgrade) expected as input"
fi

JS_EDITION=$2
if [[ "$JS_EDITION" != "ce" && "$JS_EDITION" != "pro" ]]; then
  fail "JasperReports Server edition (ce|pro) expected as input"
fi

JS_OPTION=$3
JS_ANT_TARGET=$4
JS_ANT_OPTIONS="$5 $6 $7 $8 -Djs.setup.mode=$JS_SETUP_MODE -Djava.net.preferIPv4Stack=true"

#
# Initializing time variable.
#
JS_CURRENT_TIME=`date +%Y-%m-%d_%H-%M`

#
# Defining log file name, creating log directory if it doesn't exist.
#
JS_LOG_FILE_SUFFIX=$(($RANDOM + 10000))
JS_LOG_FILE=logs/js-$JS_SETUP_MODE-$JS_EDITION\_$JS_CURRENT_TIME-$JS_LOG_FILE_SUFFIX.log
if [ ! -d logs ]; then
  mkdir -p logs
fi
echo "Writing to log file: $JS_LOG_FILE"

#
# Printing entry information.
#
log
log "Running JasperReports Server $JS_SETUP_MODE script at $JS_CURRENT_TIME"
log

export ANT_OPTS="$ANT_OPTS -Dnet.sf.ehcache.disabled=true -Xms512m -Xmx2048m"

log
log "Using ANT_OPTS: $ANT_OPTS"
log

#
# Checking JAVA_HOME.
#
if [ "$JAVA_HOME" == "" ]; then
  log "WARNING: JAVA_HOME environment variable not found"
fi

#
# Running Ant.
#
log "[$JS_OPTION]"
initializeAntEnvironment
runAnt $JS_ANT_TARGET $JS_ANT_OPTIONS
log

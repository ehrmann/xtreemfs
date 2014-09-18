#!/bin/bash
set -e

XTREEMFS_LOG_DIR=/var/log/xtreemfs
XTREEMFS_HOME=/var/lib/xtreemfs
XTREEMFS_ETC=/etc/xos/xtreemfs
XTREEMFS_USER=xtreemfs
XTREEMFS_GROUP=xtreemfs
XTREEMFS_GENERATE_UUID_SCRIPT="${XTREEMFS_ETC}/generate_uuid"

groups () {
  cat /etc/group | cut -d : -f 1
}

group-darwin () {
  dscacheutil -q group | grep ^name | sed 's/^name: //'
}

groupadd () {
  groupadd $1
}

groupadd-darwin () {
  MAXID=$(dscl . -list /Groups gid | awk '{print $2}' | sort -ug | tail -1)
  GROUPID=$((MAXID+1))
  dscl . create /Groups/$XTREEMFS_GROUP
  dscl . create /Groups/$XTREEMFS_GROUP gid $MAXID
}

useradd () {
  useradd -r --home $1 -g $2 $3
}

groupadd-darwin () {
  home=$1
  group=$2
  user=$3

  GROUPID=`dscl . -read /Groups/$group | awk '($1 == "PrimaryGroupID:") { print $2 }'`

  MAXID=$(dscl . -list /Users UniqueID | awk '{print $2}' | sort -ug | tail -1)
  USERID=$((MAXID+1))
  mkdir -p $home
  dscl . -create /Users/$user
  dscl . -create /Users/$user UserShell /bin/bash
  dscl . -create /Users/$user PrimaryGroupID $GROUPID
  dscl . -create /Users/$user NFSHomeDirectory $home
  dscl . -create /Users/$user UniqueID $USERID
  dseditgroup -o edit -a $user -t user $group
}

users () {
  cut -d : -f 1 /etc/passwd
}

users-darwin () {
  dscacheutil -q user | grep 'name:' | cut -d ' ' -f 2
}

owner () {
  stat -c %U $1
}

owner-darwin() {
  stat -f %Su $1
}

group () {
  stat -c %G $1 2>/dev/null
}

group-darwin () {
  stat -f %Sg $1 2>/dev/null
}

permissions () {
  stat -c %a $1
}

permissions-darwin () {
  stat -f %Lp $1
}

if $(echo "$OSTYPE" | grep -i -q darwin)
then
  source postinstall_setup.darwin.sh
fi

# When executed during POST installation, do not be verbose.
VERBOSE=0
script_name=$(basename "$0")
if [ "$script_name" = "postinstall_setup.sh" ]
then
  VERBOSE=1
fi

# generate UUIDs
if [ -x "$XTREEMFS_GENERATE_UUID_SCRIPT" ]; then
  for service in dir mrc osd; do
    "$XTREEMFS_GENERATE_UUID_SCRIPT" "${XTREEMFS_ETC}/${service}config.properties"
    [ $VERBOSE -eq 1 ] && echo "Generated UUID for service: $service"
  done
else
  echo "UUID can't be generated automatically. Please enter a correct UUID in each config file of an XtreemFS service."
fi


group_exists=$(groups | grep -c $XTREEMFS_GROUP || true)
if [ $group_exists -eq 0 ]; then
    groupadd $XTREEMFS_GROUP
    [ $VERBOSE -eq 1 ] && echo "created group $XTREEMFS_GROUP"
fi
exists=`users | grep -c $XTREEMFS_USER || true`
if [ $exists -eq 0 ]; then
    mkdir $XTREEMFS_HOME
    useradd $XTREEMFS_HOME $XTREEMFS_GROUP $XTREEMFS_USER
    chown $XTREEMFS_USER $XTREEMFS_HOME
    [ $VERBOSE -eq 1 ] && echo "created user $XTREEMFS_USER and data directory $XTREEMFS_HOME"
fi
if [ ! -d $XTREEMFS_HOME ]; then
    mkdir -m750 $XTREEMFS_HOME
    chown $XTREEMFS_USER $XTREEMFS_HOME
    [ $VERBOSE -eq 1 ] && echo "user $XTREEMFS_USER exists but data directory $XTREEMFS_HOME had to be created"
fi
owner=$(owner $XTREEMFS_HOME)
if [ "$owner" != "$XTREEMFS_USER" ]; then
    [ $VERBOSE -eq 1 ] && echo "directory $XTREEMFS_HOME is not owned by $XTREEMFS_USER, executing chown"
    chown $XTREEMFS_USER $XTREEMFS_HOME
fi

if [ ! -e $XTREEMFS_LOG_DIR ]; then
    mkdir $XTREEMFS_LOG_DIR
    chown -R $XTREEMFS_USER $XTREEMFS_LOG_DIR
fi

if [ -e $XTREEMFS_ETC ]; then
    group=$(group $XTREEMFS_ETC)
    if [ $group != $XTREEMFS_GROUP ]; then
        [ $VERBOSE -eq 1 ] && echo "directory $XTREEMFS_ETC is owned by $group, should be owned by $XTREEMFS_GROUP, executing chgrp (may take some time)"
        chgrp -R $XTREEMFS_GROUP $XTREEMFS_ETC
    fi
    for file in `ls $XTREEMFS_ETC/*.properties 2>/dev/null`; do
      if [ -f $file -a "$(permissions $file)" != "640" ]; then
          [ $VERBOSE -eq 1 ] && echo "setting $file 0640, executing chmod"
          chmod 0640 $file
      fi
    done
    if [ -d "$XTREEMFS_ETC/truststore/" ]
    then
        if [ "$(permissions "$XTREEMFS_ETC/truststore/")" != "750" ]
        then
            [ $VERBOSE -eq 1 ] && echo "setting $XTREEMFS_ETC/truststore/ to 0750, executing chmod (may take some time)"
            chmod -R u=rwX,g=rX,o= $XTREEMFS_ETC/truststore/
        fi
    fi
fi

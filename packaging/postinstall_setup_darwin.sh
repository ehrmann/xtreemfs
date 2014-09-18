#!/bin/bash
set -e

group () {
  dscacheutil -q group | grep ^name | sed 's/^name: //'
}

groupadd () {
  MAXID=$(dscl . -list /Groups gid | awk '{print $2}' | sort -ug | tail -1)
  GROUPID=$((MAXID+1))
  dscl . create /Groups/$XTREEMFS_GROUP
  dscl . create /Groups/$XTREEMFS_GROUP gid $MAXID
}

groupadd () {
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
  dscacheutil -q user | grep 'name:' | cut -d ' ' -f 2
}

owner () {
  stat -f %Su $1
}

group () {
  stat -f %Sg $1 2>/dev/null
}

permissions () {
  stat -f %Lp $1
}

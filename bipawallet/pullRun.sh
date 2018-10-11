#!/usr/bin/env bash
#    $0 ： ./test.sh,即命令本身，相当于C/C++中的argv[0]
#    $1 ： -f,第一个参数.
#    $2 ： config.conf
#    $3, $4 ... ：类推。
#    $#  参数的个数，不包括命令本身，上例中$#为4.
#    $@ ：参数本身的列表，也不包括命令本身，如上例为 -f config.conf -v --prefix=/home
#    $* ：和$@相同，但"$*" 和 "$@"(加引号)并不同，"$*"将所有的参数解释成一个字符串，而"$@"是一个参数数组。如下例所示：

#-eq 等于,如:if [ "$a" -eq "$b" ]
#-ne 不等于,如:if [ "$a" -ne "$b" ]
#-gt 大于,如:if [ "$a" -gt "$b" ]
#-ge 大于等于,如:if [ "$a" -ge "$b" ]
#-lt 小于,如:if [ "$a" -lt "$b" ]
#-le 小于等于,如:if [ "$a" -le "$b" ]
#<   小于(需要双括号),如:(("$a" < "$b"))
#<=  小于等于(需要双括号),如:(("$a" <= "$b"))
#>   大于(需要双括号),如:(("$a" > "$b"))
#>=  大于等于(需要双括号),如:(("$a" >= "$b"))

##################################################find apk file##################################################
# The -t flag will sort ls command output by last modified date and time, but for best results you’ll likely want to apply it with the -l long listing flag,
# and perhaps a few others as well.
filePath="./app/build/outputs/apk/debug/"
function findApkFile() {
    for file in `ls -t ${filePath}  | grep .apk`; do
        if [[ ${file} == *".apk"* ]]; then
            apkFile=$file
            break
        fi
    done
}
##################################################find devices##################################################
function findDevices() {
    devices=`adb devices`
    IFS=$'\n'
    arr=(${devices})
    unset IFS
}

function installApk() {
    for ((i=${#arr[@]}-1; i >= 0; i--)); do
        lineItemStr="${arr[$i]}"
        if [[ ${lineItemStr} == *"devices"* ]]; then
            continue
        fi
#        the command below is only legal on linux system while does not work on macOS.
#        echo ${lineItemStr} | grep -b -o device
        searchstring="device"
        rest=${lineItemStr#*$searchstring}
        pos=$(( ${#lineItemStr}-${#rest}-${#searchstring} ))-1
        if (("${pos}" <= 0)); then
            continue
        fi
        device=${lineItemStr:0:pos}
        adb -s "${device}" install -r "${filePath}${apkFile}"
#        sleep 0.050
#        continue
        adb -s "${device}" shell am start -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n com.example.jeliu.bipawallet/com.example.jeliu.bipawallet.Splash.SplashActivity
    done
    date=$(date +"%Y-%m-%d\t\t%H:%M:%SZ")
    #osascript -e 'tell app "System Events" to display dialog "script done.\nDate : '$date'" buttons {"Cancel", "Continue"} cancel button "Cancel" default button "Continue"'
    osascript -e 'display notification "script done." with title "Title" subtitle "'$date'" sound name "/System/Library/Sounds/Glass.aiff"'
}

#OLD_IFS="$IFS"
#IFS='   '
#arr=(${devices})
#IFS="$OLD_IFS"

start=$SECONDS

#git pull

function compile() {
#    ./gradlew assembleGooglePlay --info #这中方式生成的release包云信有问题，一些so不全
    ./gradlew assembleDebug --info
}

gradleTask=$(compile)
exit_status=$?
if [ "$exit_status" -eq 0 ]; then
    echo $"execute command success"
    findApkFile
    findDevices
    installApk
else
    echo $"execute command failed"
fi

duration=$(( SECONDS - start ))
echo "total time: "${duration}"s"
#./gradlew assembleRelease --info
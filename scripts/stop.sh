DEPLOYMENT_GROUP_NAME="jai-deployment-group"  # 실제 배포 그룹 이름
APPLICATION_NAME="jai-app"     # 실제 애플리케이션 이름
JAR_FILE="/home/ubuntu/jai/ToDoWithMe-0.0.1-SNAPSHOT.jar"
DEPLOY_LOG="/home/ubuntu/jai/deploy.log"
TIME_NOW=$(date +%c)

# 1. 활성화된 CodeDeploy 배포 확인 및 중단
ACTIVE_DEPLOYMENT=$(aws deploy list-deployments \
    --application-name $APPLICATION_NAME \
    --deployment-group-name $DEPLOYMENT_GROUP_NAME \
    --include-only-statuses "InProgress" "Queued" \
    --query "deployments[0]" \
    --output text)

if [ "$ACTIVE_DEPLOYMENT" != "None" ]; then
    echo "$TIME_NOW > Active deployment found: $ACTIVE_DEPLOYMENT. Stopping it." >> $DEPLOY_LOG
    aws deploy stop-deployment --deployment-id $ACTIVE_DEPLOYMENT --auto-rollback-enabled
else
    echo "$TIME_NOW > No active deployment found." >> $DEPLOY_LOG
fi

# 2. 현재 실행 중인 애플리케이션 프로세스 종료
CURRENT_PID=$(ps -ef | grep java | grep "$JAR_FILE" | awk '{print $2}')

if [ -z "$CURRENT_PID" ]; then
    echo "$TIME_NOW > No application process running." >> $DEPLOY_LOG
else
    echo "$TIME_NOW > Stopping application process: $CURRENT_PID." >> $DEPLOY_LOG
    kill -15 $CURRENT_PID
    sleep 5
fi

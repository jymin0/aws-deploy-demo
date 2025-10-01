# awscli 설치
# mac - https://awscli.amazonaws.com/AWSCLIV2.pkg
# win - https://awscli.amazonaws.com/AWSCLIV2.msi

# mac은 brew를 통해 설치도 가능!
brew install awscli
aws --version

# 로그인 수행하기
# 1. access key 등록 - 본인키 - AKIASU7N7NOSTXLLNY76
# 2. secret key 등록 - 본인 비밀키 - 7FSSPhJhk9DaiUqfWYri8DbLGPtWr/7ScROuEs70
# 3. region 등록 - ap-northeast-2
# 4. format 등록 - json
aws configure

# 정상 설정 확인
aws sts get-caller-identity

# ECR 로그인 (AWS CLI v2 기준)
aws ecr get-login-password --region ap-northeast-2 \
 | docker login --username AWS --password-stdin \
 {AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com


aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 155765116803.dkr.ecr.ap-northeast-2.amazonaws.com

# ECR 리포지토리 생성(있으면 생성 안해도 됨)
aws ecr create-repository --region ap-northeast-2 --repository-name dev/my-repo

# dockerfile 빌드
docker build -t file-management-app:1.0.0 .

# 로컬에서 빌드한 이미지에 ECR 태그 지정
docker tag {appname:tag} \
 {AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/{repo}/{appname:tag}
docker tag file-management-app:1.0.0 182498323365.dkr.ecr.ap-northeast-2.amazonaws.com/dev/my-repo:file-management-app-1.0.0

# 이미지를 ECR로 푸시
docker push {AWS_ACCOUNT_ID}.dkr.ecr.ap-northeast-2.amazonaws.com/{repo}/{appname}
docker push 182498323365.dkr.ecr.ap-northeast-2.amazonaws.com/dev/my-repo:file-management-app-1.0.0

# ECR 리포지토리의 이미지 목록 확인
aws ecr describe-images --repository-name dev/my-repo --region ap-northeast-2

# 1.0.0 → production 태그 복제
aws ecr put-image \
  --repository-name dev/my-repo \
  --image-tag production \
  --image-manifest "$(aws ecr batch-get-image \
      --repository-name dev/my-repo \
      --image-ids imageTag=file-management-app-1.0.0 \
      --query 'images[0].imageManifest' \
      --output text)" \
  --region ap-northeast-2
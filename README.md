# 누굴까 안드로이드 애플리케이션
Leader : 이후승,
email : dbfoot95@gmail.com  
member : 박가혜,
email : gahye0530@gmail.com<br>
Demo Video : https://www.youtube.com/watch?v=8tPPj9rE23g  
Presentation : https://docs.google.com/presentation/d/1MnkOVfzcFpYsd7S-YSgUd5UwZax6rVwxAK3N6fnDOsA/edit#slide=id.p1  
GitBook : https://undefined-189.gitbook.io/who_am_i/  
Figma : https://www.figma.com/file/eIxiGbryXi4QKPTeQQPFLe/Who-am-I-Project?node-id=0%3A1  

## Introduction
호기심이 많은 유아기 1~6세 아이들이 궁금한 것을 바로 스마트폰으로 촬영하고 확인하며 눈과 귀로 보고 들으며 빠르게 습득할 수 있는 교육용 애플리케이션입니다.  
예를 들면 피아노가 있는 사진을 촬영했을 때 아이에게 피아노 라고 알려주며 음성으로 친근하게 해당 객체가 무엇인지 알려줍니다.  
이 때 사용되는 기술이 객체 인식 인공지능 기술 AWS Rekognition과 텍스트를 음성으로 변환시켜주는 안드로이드 TTS 기술을 활용합니다.  
또한 피아노와 관련된 검색결과를 동영상으로 보여 줌으로써 아이가 해당 객체가 무엇인지 시각으로 확인할 수 있습니다.  
이는 유튜브 검색 API 연동을 통해 제공합니다. 회원 유저는 촬영한 사진을 다른 사용자가 볼 수 있도록 게시판에 업로드 할 수 있습니다.  

<br>

  1) 필요한 권한을 요청합니다. (카메라, 외부저장소, 오디오)
  2) 사진 촬영 또는 앨범에서 사진을 가져옵니다. 
  3) AWS Rekognition 통해 사물을 분석합니다.
  4) 분석 결과를 Naver Papago로 번역합니다. 
  5) 번역된 분석결과를 TTS로 음성지원 합니다. 
  6) 번역된 분석결과를 Youtube로 검색 합니다. 


### <화면계획>
<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbi3gRt%2FbtryDa2FkcT%2FGlIYV6x158I8qypQ5Gl3l1%2Fimg.jpg"></img>

### <사이트맵>
<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbubryR%2FbtryDPYflYv%2FM9sqheLOMMPrHKotKgqe10%2Fimg.jpg"></img>

## Development Environment
- MySql Workbench 8.0.27
- Python 3.8 & Serverless v3
- Android Studio Bumblebee | 2021.1.1 Patch 2

## Application Version
- minSdKVersion : 16
- targetSdkVersion : 30

## APIs
- Retrofit : https://square.github.io/retrofit/
- Glide : https://github.com/bumptech/glide
- Youtube Data Api - Search : https://developers.google.com/youtube/v3/docs/search

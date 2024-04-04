# Welcome

This repository contains the source-code for *baseModX*, a web application implementing *BASE* to store details of proposed 
*biometric authentication systems and their evaluations* in a central repository. 


![Characteristics Selection](/screenshots/characteristics_selection.png "4-step process to conserve all details of a proposed system and its evaluations.")


If you wonder why you would do that, please have a look at the following research paper. Please do not forget to leave
a citation if you are using it or baseModX for your own research ;)

```
@INPROCEEDINGS{klieme_base_2023,
  author={Klieme, Eric and Meinel, Christoph},
  booktitle={2023 IEEE International Joint Conference on Biometrics (IJCB)}, 
  title={All about that BASE: Modeling Biometric Authentication Systems and their Evaluations to enable a more efficient Exchange of Research Results}, 
  year={2023},
  pages={1-10},
  keywords={Biometrics (access control);Biological system modeling;Authentication;Memory;Manuals;Security;Usability},
  doi={10.1109/IJCB57857.2023.10448589}
}

```
# Requirements

The project runs using spring-boot, vuejs/vuetify and mongodb. So please make sure to have a proper npm / node version,
jdk and mongoDB installed on your machine (or on your favorite deployment machine).

# Building baseModX

In general, this is a maven project. The frontend and backend can be built
independent from each other, please refer to the respective package.json or pom.xml. For a final build for prod deployment,
please take also a look at the pom.xml in the parent folder.

## Backend
Please do not forget to copy and rename the `application.properties.example` to `application.properties` and replace your details.
For now, you have to provide the environt variables `SPRING_DATA_MONGODB_URI`, `BASEMODX_DATABASE_NAME` (should be the same as the last part of the mongodb uri)
, `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET` (both for login), and `REDIRECT_URI` (should be the place where the backend runs).

## Frontend

Similar to the backend, you have to rename the `.env.example` to `.env` and fill the respective details.




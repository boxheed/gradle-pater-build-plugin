FROM node:22-trixie-slim

USER root

ARG USERNAME=vscode
ARG USER_UID=1001
ARG USER_GID=1001

RUN groupadd --gid $USER_GID $USERNAME \
  && useradd -s /bin/bash --uid $USER_UID --gid $USER_GID -m $USERNAME

ADD install_packages /usr/bin

RUN chmod +x /usr/bin/install_packages

RUN install_packages \
  git \
  sudo \
  curl \
  wget \
  jq \
  zip \
  openssh-client \
  procps \
  openjdk-21-jdk \
  python3-venv \
  python3-pip \
  pipx

RUN echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
  && chmod 0440 /etc/sudoers.d/$USERNAME

USER vscode

RUN echo "source /home/vscode/.env/setenv.sh" >> ~/.profile


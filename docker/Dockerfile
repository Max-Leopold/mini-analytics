FROM python:3
ADD create-collection.py /create-collection.py
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.5.1/wait /wait
RUN chmod +x /wait
CMD /wait && python /create-collection.py



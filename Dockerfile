FROM hseeberger/scala-sbt
WORKDIR /Webscraper
ADD . /Webscraper
CMD sbt run 60 30
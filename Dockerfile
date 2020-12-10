FROM quay.io/quarkus/centos-quarkus-maven:20.2.0-java11 as build
WORKDIR /project
# Download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline
# Build app
COPY src/ /project/src/
RUN mvn clean install -Pnative -DskipTests=true

FROM public.ecr.aws/lambda/provided:al2 AS runtime
# Copy custom runtime bootstrap
COPY --from=build /project/target/bootstrap-example.sh ${LAMBDA_RUNTIME_DIR}/bootstrap
# Copy function code
COPY --from=build /project/target/*-runner ${LAMBDA_TASK_ROOT}/runner

ENV DISABLE_SIGNAL_HANDLERS true

# Set the CMD to your handler (could also be done as a parameter override outside of the Dockerfile)
CMD [ "no.handler" ]

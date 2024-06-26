package com.redhat.training.bank.stream;

import com.redhat.training.bank.event.AmountWasDeposited;
import com.redhat.training.bank.event.HighValueDepositWasDetected;
import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class AmountWasDepositedPipeline extends StreamProcessor {
    private static final Logger LOGGER = Logger.getLogger(AmountWasDepositedPipeline.class);

    // Reading topic
    static final String AMOUNT_WAS_DEPOSITED_TOPIC = "bank-account-deposit";

    // Writing topic
    static final String HIGH_VALUE_DEPOSIT_TOPIC = "high-value-deposit-alert";

    private KafkaStreams streams;

    void onStart(@Observes StartupEvent startupEvent) {
    //@Produces
    //@Named("amountWasDepositedTopology")
    //public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        ObjectMapperSerde<AmountWasDeposited> depositEventSerde
                = new ObjectMapperSerde<>(AmountWasDeposited.class);

        ObjectMapperSerde<HighValueDepositWasDetected> highValueEventSerde
                = new ObjectMapperSerde<>(HighValueDepositWasDetected.class);

        // TODO: Build the stream topology
        builder.stream(
            AMOUNT_WAS_DEPOSITED_TOPIC,
            Consumed.with(Serdes.Long(), depositEventSerde)
        ).filter(
            (key, deposit) -> deposit.amount > 1000
        ).map((key, deposit) -> {
            logHighValueDepositAlert(deposit.bankAccountId, deposit.amount);

            return new KeyValue<>(
                deposit.bankAccountId,
                new HighValueDepositWasDetected(deposit.bankAccountId, deposit.amount)
            );
        }).to(
            HIGH_VALUE_DEPOSIT_TOPIC,
            Produced.with(Serdes.Long(), highValueEventSerde)
        );

        //return builder.build();
        // TODO: Create a Kafka streams and start it
        streams = new KafkaStreams(
            builder.build(),
            generateStreamConfig()
        );

        streams.start();
    }

    void onStop(@Observes ShutdownEvent shutdownEvent) {
        // TODO: Close the stream on shutdown
        streams.close();
    }

    // Helper methods
    private void logHighValueDepositAlert(Long bankAccountId, Long amount) {
        LOGGER.infov(
                "HighValueDepositWasDetected - Account ID: {0} Amount: {1}",
                bankAccountId,
                amount
        );
    }
}

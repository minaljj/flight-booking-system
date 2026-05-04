const { Kafka } = require('kafkajs');
const emailService = require('./emailService');
const emailTemplates = require('./emailTemplates');

let broker = process.env.KAFKA_BROKER;

const kafka = new Kafka({
  clientId: 'notification-service',
  brokers: [broker]
});

const consumer = kafka.consumer({ groupId: 'notification-group' });

const run = async () => {
  await consumer.connect();
  await consumer.subscribe({ topic: 'notification-send', fromBeginning: true });
  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      try {
        const payload = JSON.parse(message.value.toString());
        console.log('Received notification via Kafka:', payload);
        const { type, email, phone, pnr, seats } = payload;
        if (type === 'BOOKING_CONFIRMED') {
          const text = `Your booking (PNR: ${pnr}) for ${seats} seats is confirmed!`;
          const html = emailTemplates.getConfirmationTemplate(pnr, seats);
          await emailService.sendEmail(email, 'Booking Confirmed', text, html);
        } else if (type === 'BOOKING_CANCELLED') {
          const text = `Your booking (PNR: ${pnr}) has been cancelled.`;
          const html = emailTemplates.getCancellationTemplate(pnr);
          await emailService.sendEmail(email, 'Booking Cancelled', text, html);

        }
      } catch (err) {
        console.error('Error processing Kafka message:', err);
      }
    },
  });
};

module.exports = { run };

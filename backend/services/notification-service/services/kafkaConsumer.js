const { Kafka } = require('kafkajs');
const emailService = require('./emailService');
//const { sendSms } = require('./smsService');
const emailTemplates = require('./emailTemplates');

const kafka = new Kafka({
  clientId: 'notification-service',
  brokers: [process.env.KAFKA_BROKER || '127.0.0.1:9092']
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
          //await fast2smsService.sendSms(phone, `Flight App: Booking PNR ${pnr} Confirmed!`);
        } else if (type === 'BOOKING_CANCELLED') {
          const text = `Your booking (PNR: ${pnr}) has been cancelled.`;
          const html = emailTemplates.getCancellationTemplate(pnr);
          await emailService.sendEmail(email, 'Booking Cancelled', text, html);
          //await fast2smsService.sendSms(phone, `Flight App: Booking PNR ${pnr} Cancelled.`);
        }
      } catch (err) {
        console.error('Error processing Kafka message:', err);
      }
    },
  });
};

module.exports = { run };

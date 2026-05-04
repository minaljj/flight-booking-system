const express = require('express');
const router = express.Router();
const emailService = require('../services/emailService');
const smsService = require('../services/smsService');

router.post('/send', async (req, res) => {
  try {
    const { type, email, phone, pnr, seats } = req.body;
    console.log(`Received notification request:`, req.body);

    if (type === 'BOOKING_CONFIRMED') {
      await emailService.sendEmail(email, 'Booking Confirmed', `Your booking (PNR: ${pnr}) for ${seats} seats is confirmed!`);
      await smsService.sendSms(phone, `Flight App: Booking PNR ${pnr} Confirmed!`);
    } else if (type === 'BOOKING_CANCELLED') {
      await emailService.sendEmail(email, 'Booking Cancelled', `Your booking (PNR: ${pnr}) has been cancelled.`);
      await smsService.sendSms(phone, `Flight App: Booking PNR ${pnr} Cancelled.`);
    }

    res.json({ success: true, message: 'Notification dispatched' });
  } catch (err) {
    console.error('Error dispatching notification:', err);
    res.status(500).json({ success: false, error: err.message });
  }
});

module.exports = router;
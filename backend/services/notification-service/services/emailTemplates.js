require('dotenv').config();

const getConfirmationTemplate = (pnr, seats) => {
    const baseUrl = process.env.FRONTEND_URL || 'http://localhost:5173';

    return `
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        @media screen and (max-width: 600px) {
            .container { width: 100% !important; border-radius: 0 !important; }
            .content { padding: 24px !important; }
            .header { padding: 32px 16px !important; }
            .pnr-display { font-size: 32px !important; }
        }
    </style>
</head>
<body style="margin: 0; padding: 0; background-color: #f8fafc;">
    <div class="container" style="font-family: 'Inter', system-ui, -apple-system, sans-serif; max-width: 600px; margin: 20px auto; background-color: #ffffff; border: 1px solid #f1f5f9; border-radius: 24px; overflow: hidden;">
        <div class="header" style="background-color: #2563eb; padding: 40px 20px; text-align: center;">
            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: 900; letter-spacing: -0.025em; text-transform: uppercase;">Flight Confirmed</h1>
            <p style="color: #bfdbfe; margin-top: 8px; font-weight: 600; text-transform: uppercase; font-size: 12px; letter-spacing: 0.1em;">Boarding Pass Ready for Dispatch</p>
        </div>
        <div class="content" style="padding: 40px; text-align: center;">
            <div style="background-color: #f8fafc; border-radius: 16px; padding: 24px; margin-bottom: 32px; border: 1px dashed #e2e8f0;">
                <p style="color: #64748b; font-size: 12px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.1em; margin: 0 0 8px 0;">Booking Reference (PNR)</p>
                <h2 class="pnr-display" style="color: #0f172a; font-size: 42px; font-weight: 900; margin: 0; letter-spacing: 0.05em;">${pnr}</h2>
            </div>
            <div style="margin-bottom: 32px;">
                <div style="display: inline-block; background: #f0fdf4; padding: 12px 24px; border-radius: 12px; text-align: center;">
                    <span style="display: block; font-size: 10px; color: #166534; font-weight: 800; text-transform: uppercase;">Seat Manifest</span>
                    <span style="font-size: 18px; color: #166534; font-weight: 900;">${seats} Passengers</span>
                </div>
            </div>
            <p style="color: #475569; line-height: 1.6; margin-bottom: 32px; font-weight: 500;">Your flight reservation has been successfully processed and synchronized with our global fleet management system.</p>
            <a href="${baseUrl}/ticket/${pnr}" style="background-color: #0f172a; color: #ffffff; padding: 16px 32px; border-radius: 12px; text-decoration: none; font-weight: 800; font-size: 14px; display: inline-block; text-transform: uppercase; letter-spacing: 0.05em;">Manage My Booking</a>
        </div>
        <div style="background-color: #f8fafc; padding: 20px; text-align: center; border-top: 1px solid #f1f5f9;">
            <p style="color: #94a3b8; font-size: 11px; font-weight: 600;">© 2026 FlightApp Global Systems. All rights reserved.</p>
        </div>
    </div>
</body>
</html>
`;
};

const getCancellationTemplate = (pnr) => {
    const baseUrl = process.env.FRONTEND_URL || 'http://localhost:5173';

    return `
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        @media screen and (max-width: 600px) {
            .container { width: 100% !important; border-radius: 0 !important; }
            .content { padding: 24px !important; }
            .pnr-display { font-size: 32px !important; }
        }
    </style>
</head>
<body style="margin: 0; padding: 0; background-color: #f8fafc;">
    <div class="container" style="font-family: 'Inter', system-ui, -apple-system, sans-serif; max-width: 600px; margin: 20px auto; background-color: #ffffff; border: 1px solid #f1f5f9; border-radius: 24px; overflow: hidden;">
        <div style="background-color: #e11d48; padding: 40px 20px; text-align: center;">
            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: 900; letter-spacing: -0.025em; text-transform: uppercase;">Booking Cancelled</h1>
        </div>
        <div class="content" style="padding: 40px; text-align: center;">
            <div style="background-color: #fff1f2; border-radius: 16px; padding: 24px; margin-bottom: 32px; border: 1px dashed #fecdd3;">
                <p style="color: #e11d48; font-size: 12px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.1em; margin: 0 0 8px 0;">Reference (PNR)</p>
                <h2 class="pnr-display" style="color: #9f1239; font-size: 42px; font-weight: 900; margin: 0; letter-spacing: 0.05em;">${pnr}</h2>
            </div>
            <p style="color: #475569; line-height: 1.6; margin-bottom: 32px; font-weight: 500;">The rotation for PNR <b>${pnr}</b> has been de-synchronized and cancelled as per your request.</p>
            <p style="color: #94a3b8; font-size: 13px;">If this was a mistake, please visit our <a href="${baseUrl}/history" style="color: #e11d48; font-weight: bold;">booking history</a> portal immediately.</p>
        </div>
    </div>
</body>
</html>
`;
};

module.exports = {
    getConfirmationTemplate,
    getCancellationTemplate
};

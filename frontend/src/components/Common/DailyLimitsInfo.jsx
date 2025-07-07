import React from 'react';
import { Alert, Typography, Box } from '@mui/material';
import { Info } from '@mui/icons-material';

const DailyLimitsInfo = ({ mode = "management" }) => {
    const getLimitsByMode = () => {
        switch (mode) {
            case "edit":
                return [
                    "You can update product prices up to 2 times per day",
                    "You cannot update more than 30 products per day",
                    "Price must be between 30% and 150% of product value"
                ];
            case "add":
                return [
                    "You can create unlimited products per day",
                    "You cannot update more than 30 products per day",
                    "Price must be between 30% and 150% of product value"
                ];
            case "delete":
                return [
                    "You can delete up to 10 products at once",
                    "You cannot delete more than 30 products per day for security reasons"
                ];
            case "management":
            default:
                return [
                    "✅ Create: Unlimited products per day",
                    "⚠️ Update: Maximum 30 products per day",
                    "⚠️ Delete: Maximum 30 products per day (up to 10 at once)",
                    "⚠️ Price Changes: Maximum 2 price updates per product per day",
                    "⚠️ Price Range: Must be between 30% and 150% of product value"
                ];
        }
    };

    const limits = getLimitsByMode();

    return (
        <Alert severity="info" icon={<Info />} sx={{ mb: 2 }}>
            <Typography variant="body2">
                <strong>Daily Limits:</strong>
                <ul style={{ margin: "8px 0 0 0", paddingLeft: "20px" }}>
                    {limits.map((limit, index) => (
                        <li key={index}>{limit}</li>
                    ))}
                </ul>
            </Typography>
        </Alert>
    );
};

export default DailyLimitsInfo; 
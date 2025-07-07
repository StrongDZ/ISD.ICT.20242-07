import React from 'react';
import { Alert, Typography, Box } from '@mui/material';
import { Warning, Error, Info } from '@mui/icons-material';

const ErrorDisplay = ({ error, type = "error" }) => {
    if (!error) return null;

    const getIcon = () => {
        switch (type) {
            case "warning":
                return <Warning />;
            case "info":
                return <Info />;
            default:
                return <Error />;
        }
    };

    const getSeverity = () => {
        switch (type) {
            case "warning":
                return "warning";
            case "info":
                return "info";
            default:
                return "error";
        }
    };

    return (
        <Alert severity={getSeverity()} icon={getIcon()} sx={{ mb: 2 }}>
            <Typography variant="body2">
                {error}
            </Typography>
        </Alert>
    );
};

export default ErrorDisplay; 
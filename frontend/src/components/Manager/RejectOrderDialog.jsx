import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Typography,
    Box
} from '@mui/material';

const RejectOrderDialog = ({ open, onClose, onReject, orderId }) => {
    const [reason, setReason] = useState('');
    const [customReason, setCustomReason] = useState('');
    const [loading, setLoading] = useState(false);

    const predefinedReasons = [
        'Insufficient stock in warehouse',
        'Invalid delivery address information',
        'Product technical defect',
        'Customer cancellation request',
        'Exceeds order limit',
        'Invalid payment information',
        'Product discontinued',
        'Delivery address outside service area',
        'Incomplete customer information',
        'Product does not match description',
        'Other'
    ];

    const handleReject = async () => {
        if (!reason && !customReason.trim()) {
            alert('Please select or enter a rejection reason');
            return;
        }

        setLoading(true);
        try {
            const finalReason = reason === 'Other' ? customReason : reason;
            await onReject(orderId, finalReason);
            handleClose();
        } catch (error) {
            console.error('Error rejecting order:', error);
            alert('An error occurred while rejecting the order');
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        setReason('');
        setCustomReason('');
        setLoading(false);
        onClose();
    };

    return (
        <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
            <DialogTitle>
                Reject Order #{orderId}
            </DialogTitle>
            <DialogContent>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    Please select or enter a reason for rejecting this order.
                </Typography>

                <FormControl fullWidth sx={{ mb: 2 }}>
                    <InputLabel>Rejection Reason</InputLabel>
                    <Select
                        value={reason}
                        onChange={(e) => setReason(e.target.value)}
                        label="Rejection Reason"
                    >
                        <MenuItem value="">
                            <em>Select rejection reason</em>
                        </MenuItem>
                        {predefinedReasons.map((predefinedReason) => (
                            <MenuItem key={predefinedReason} value={predefinedReason}>
                                {predefinedReason}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                {reason === 'Other' && (
                        <TextField
                            fullWidth
                        label="Enter rejection reason"
                        value={customReason}
                        onChange={(e) => setCustomReason(e.target.value)}
                            multiline
                            rows={3}
                        placeholder="Enter detailed rejection reason..."
                        sx={{ mb: 2 }}
                    />
                )}

                <Box sx={{ mt: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                        <strong>Note:</strong> Rejecting this order will update the order status to "Cancelled" 
                        and save the rejection reason in the system for tracking purposes.
                    </Typography>
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} disabled={loading}>
                    Cancel
                </Button>
                <Button 
                    onClick={handleReject} 
                    variant="contained" 
                    color="error"
                    disabled={loading || (!reason && !customReason.trim())}
                >
                    {loading ? 'Processing...' : 'Reject Order'}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default RejectOrderDialog;

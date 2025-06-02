import React, { useState, useEffect } from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    TextField,
    Box,
    Typography,
    Alert,
} from "@mui/material";
import { ThumbDown, Cancel } from "@mui/icons-material";

const RejectOrderDialog = ({ open, onClose, order, onConfirm }) => {
    const [formData, setFormData] = useState({
        reason: "",
        customReason: "",
    });

    // Predefined rejection reasons
    const rejectionReasons = [
        { value: "insufficient_stock", label: "Insufficient Stock" },
        { value: "product_discontinued", label: "Product Discontinued" },
        { value: "payment_failed", label: "Payment Verification Failed" },
        { value: "invalid_address", label: "Invalid Delivery Address" },
        { value: "customer_request", label: "Customer Cancellation Request" },
        { value: "duplicate_order", label: "Duplicate Order" },
        { value: "system_error", label: "System Error" },
        { value: "quality_issue", label: "Product Quality Issue" },
        { value: "other", label: "Other (Please specify)" },
    ];

    // Reset form when dialog opens/closes
    useEffect(() => {
        if (open) {
            setFormData({ reason: "", customReason: "" });
        }
    }, [open]);

    const handleReasonChange = (value) => {
        setFormData({
            reason: value,
            customReason: value !== "other" ? "" : formData.customReason,
        });
    };

    const handleCustomReasonChange = (value) => {
        setFormData({
            ...formData,
            customReason: value,
        });
    };

    const handleConfirm = () => {
        // Validation
        if (!formData.reason) {
            return; // Button should be disabled, but extra safety
        }

        if (formData.reason === "other" && !formData.customReason.trim()) {
            return; // Button should be disabled, but extra safety
        }

        // Get final reason text
        const finalReason =
            formData.reason === "other" ? formData.customReason.trim() : rejectionReasons.find((r) => r.value === formData.reason)?.label;

        // Call parent callback with order and reason
        onConfirm(order, finalReason);
    };

    const handleCancel = () => {
        setFormData({ reason: "", customReason: "" });
        onClose();
    };

    const isValid = () => {
        if (!formData.reason) return false;
        if (formData.reason === "other" && !formData.customReason.trim()) return false;
        return true;
    };

    return (
        <Dialog open={open} onClose={handleCancel} maxWidth="sm" fullWidth>
            <DialogTitle>
                <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                    <ThumbDown color="error" />
                    <Typography variant="h6">Reject Order</Typography>
                </Box>
            </DialogTitle>

            <DialogContent>
                <Box sx={{ mt: 2 }}>
                    <Alert severity="warning" sx={{ mb: 3 }}>
                        You are about to reject order <strong>{order?.orderID}</strong>. Please select a reason that will be communicated to the
                        customer.
                    </Alert>

                    <FormControl fullWidth sx={{ mb: 3 }}>
                        <InputLabel>Reason for Rejection *</InputLabel>
                        <Select value={formData.reason} onChange={(e) => handleReasonChange(e.target.value)} label="Reason for Rejection *" required>
                            {rejectionReasons.map((reason) => (
                                <MenuItem key={reason.value} value={reason.value}>
                                    {reason.label}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    {/* Custom reason field - only show when "Other" is selected */}
                    {formData.reason === "other" && (
                        <TextField
                            fullWidth
                            label="Custom Reason *"
                            multiline
                            rows={3}
                            value={formData.customReason}
                            onChange={(e) => handleCustomReasonChange(e.target.value)}
                            placeholder="Please specify the reason for rejection..."
                            required
                            error={formData.reason === "other" && !formData.customReason.trim()}
                            helperText={
                                formData.reason === "other" && !formData.customReason.trim()
                                    ? "Custom reason is required when 'Other' is selected"
                                    : "This reason will be sent to the customer"
                            }
                        />
                    )}
                </Box>
            </DialogContent>

            <DialogActions>
                <Button onClick={handleCancel} startIcon={<Cancel />}>
                    Cancel
                </Button>
                <Button onClick={handleConfirm} variant="contained" color="error" startIcon={<ThumbDown />} disabled={!isValid()}>
                    Reject Order
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default RejectOrderDialog;

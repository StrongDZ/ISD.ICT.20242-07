import React from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    Grid,
    Box,
    Typography,
    Chip,
    Divider,
    Card,
    CardContent,
    List,
    ListItem,
    ListItemAvatar,
    Avatar,
    ListItemText,
} from "@mui/material";
import { Cancel, Receipt, Person, Phone, LocationOn } from "@mui/icons-material";

const OrderDialog = ({ open, onClose, order, mode = "view" }) => {
    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleString("vi-VN");
    };

    const getStatusColor = (status) => {
        switch (status) {
            case "PENDING_APPROVAL":
                return "warning";
            case "PROCESSING":
                return "info";
            case "SHIPPING":
                return "primary";
            case "DELIVERED":
                return "success";
            case "CANCELLED":
                return "error";
            default:
                return "default";
        }
    };

    const getStatusDisplayName = (status) => {
        switch (status) {
            case "PENDING_APPROVAL":
                return "Pending Approval";
            case "PROCESSING":
                return "Processing";
            case "SHIPPING":
                return "Shipping";
            case "DELIVERED":
                return "Delivered";
            case "CANCELLED":
                return "Cancelled";
            default:
                return status;
        }
    };

    const getDialogTitle = () => {
        return "Order Details";
    };

    const calculateTotals = () => {
        if (!order?.items) return { subtotal: 0, total: 0 };

        const subtotal = order.items.reduce((sum, item) => {
            return sum + item.price * item.quantity;
        }, 0);

        const deliveryFee = order.deliveryFee || 50000;
        const vat = subtotal * 0.1;
        const total = subtotal + deliveryFee + vat;

        return { subtotal, deliveryFee, vat, total };
    };

    const totals = calculateTotals();

    if (!order) return null;

    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>
                <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <Typography variant="h6">{getDialogTitle()}</Typography>
                    <Box sx={{ display: "flex", gap: 1 }}>
                        <Chip label={order.orderID} color="primary" size="small" icon={<Receipt />} />
                        <Chip label={getStatusDisplayName(order.status)} color={getStatusColor(order.status)} size="small" />
                    </Box>
                </Box>
            </DialogTitle>

            <DialogContent>
                <Grid container spacing={3}>
                    {/* Order Information */}
                    <Grid item xs={12} md={6}>
                        <Card variant="outlined">
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    <Receipt sx={{ mr: 1, verticalAlign: "middle" }} />
                                    Order Information
                                </Typography>
                                <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                                    <Typography variant="body2">
                                        <strong>Order ID:</strong> {order.orderID}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Order Date:</strong> {formatDate(order.orderDate)}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Total Amount:</strong> {formatPrice(order.totalAmount)}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Payment Method:</strong> {order.paymentMethod || "Cash on Delivery"}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Delivery Type:</strong> {order.isRushOrder ? "Rush Delivery" : "Standard Delivery"}
                                    </Typography>
                                </Box>
                            </CardContent>
                        </Card>
                    </Grid>

                    {/* Customer Information */}
                    <Grid item xs={12} md={6}>
                        <Card variant="outlined">
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    <Person sx={{ mr: 1, verticalAlign: "middle" }} />
                                    Customer Information
                                </Typography>
                                <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                                    <Typography variant="body2">
                                        <strong>Name:</strong> {order.customerName}
                                    </Typography>
                                    <Typography variant="body2">
                                        <Phone sx={{ mr: 1, fontSize: 16, verticalAlign: "middle" }} />
                                        <strong>Phone:</strong> {order.customerPhone}
                                    </Typography>
                                    {order.customerEmail && (
                                        <Typography variant="body2">
                                            <strong>Email:</strong> {order.customerEmail}
                                        </Typography>
                                    )}
                                    <Typography variant="body2">
                                        <LocationOn sx={{ mr: 1, fontSize: 16, verticalAlign: "middle" }} />
                                        <strong>Address:</strong> {order.deliveryAddress}
                                    </Typography>
                                </Box>
                            </CardContent>
                        </Card>
                    </Grid>

                    {/* Order Items */}
                    <Grid item xs={12}>
                        <Card variant="outlined">
                            <CardContent>
                                <Typography variant="h6" gutterBottom>
                                    Order Items ({order.items?.length || 0} items)
                                </Typography>

                                {order.items && order.items.length > 0 ? (
                                    <List>
                                        {order.items.map((item, index) => (
                                            <ListItem key={index} sx={{ px: 0 }}>
                                                <ListItemAvatar>
                                                    <Avatar src={item.imageURL} alt={item.title} sx={{ width: 56, height: 56 }} />
                                                </ListItemAvatar>
                                                <ListItemText
                                                    primary={<Typography variant="body1">{item.title}</Typography>}
                                                    secondary={
                                                        <Box>
                                                            <Typography variant="body2" color="text.secondary">
                                                                {formatPrice(item.price)} × {item.quantity}
                                                            </Typography>
                                                            {item.category && (
                                                                <Chip
                                                                    label={item.category.toUpperCase()}
                                                                    size="small"
                                                                    variant="outlined"
                                                                    sx={{ mt: 0.5 }}
                                                                />
                                                            )}
                                                        </Box>
                                                    }
                                                />
                                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                                    {formatPrice(item.price * item.quantity)}
                                                </Typography>
                                            </ListItem>
                                        ))}
                                    </List>
                                ) : (
                                    <Typography variant="body2" color="text.secondary">
                                        No items found for this order.
                                    </Typography>
                                )}

                                <Divider sx={{ my: 2 }} />

                                {/* Order Totals */}
                                <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                                    <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                                        <Typography variant="body2">Subtotal:</Typography>
                                        <Typography variant="body2">{formatPrice(totals.subtotal)}</Typography>
                                    </Box>
                                    <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                                        <Typography variant="body2">Delivery Fee:</Typography>
                                        <Typography variant="body2">{formatPrice(totals.deliveryFee)}</Typography>
                                    </Box>
                                    <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                                        <Typography variant="body2">VAT (10%):</Typography>
                                        <Typography variant="body2">{formatPrice(totals.vat)}</Typography>
                                    </Box>
                                    <Divider />
                                    <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                                        <Typography variant="h6" sx={{ fontWeight: "bold" }}>
                                            Total:
                                        </Typography>
                                        <Typography variant="h6" sx={{ fontWeight: "bold", color: "primary.main" }}>
                                            {formatPrice(totals.total)}
                                        </Typography>
                                    </Box>
                                </Box>
                            </CardContent>
                        </Card>
                    </Grid>

                    {/* Order Notes */}
                    {order.notes && (
                        <Grid item xs={12}>
                            <Card variant="outlined">
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>
                                        Order Notes
                                    </Typography>
                                    <Typography variant="body2">{order.notes}</Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    )}
                </Grid>
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} startIcon={<Cancel />}>
                    Close
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default OrderDialog;

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
            case "PENDING":
                return "warning";
            case "APPROVED":
                return "success";
            case "REJECTED":
            case "CANCELLED":
                return "error";
            default:
                return "default";
        }
    };

    const getStatusDisplayName = (status) => {
        switch (status) {
            case "PENDING":
                return "Pending";
            case "APPROVED":
                return "Approved";
            case "REJECTED":
                return "Rejected";
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
        // Use totalPrice from API if available, otherwise calculate from items
        const totalFromAPI = order?.totalPrice;
        if (totalFromAPI && !order?.items) {
            return { 
                subtotal: totalFromAPI, 
                deliveryFee: 0, 
                vat: 0, 
                total: totalFromAPI 
            };
        }

        if (!order?.items) return { subtotal: 0, total: 0 };

        const subtotal = order.items.reduce((sum, item) => {
            const price = item.productPrice || item.price || 0;
            return sum + price * item.quantity;
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
                        <Chip label={order.id || order.orderID || 'N/A'} color="primary" size="small" icon={<Receipt />} />
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
                                        <strong>Order ID:</strong> {order.id || order.orderID || 'N/A'}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Order Date:</strong> {order.orderDate ? formatDate(order.orderDate) : 'N/A'}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Total Amount:</strong> {formatPrice(order.totalPrice || order.totalAmount || 0)}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Payment Method:</strong> {order.paymentMethod || "Cash on Delivery"}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Delivery Type:</strong> {(order.deliveryInfo?.isRushOrder || order.isRushOrder) ? "Rush Delivery" : "Standard Delivery"}
                                    </Typography>
                                    <Typography variant="body2">
                                        <strong>Status:</strong> {order.status || 'N/A'}
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
                                        <strong>Name:</strong> {order.deliveryInfo?.recipientName || order.customerName || 'N/A'}
                                    </Typography>
                                    <Typography variant="body2">
                                        <Phone sx={{ mr: 1, fontSize: 16, verticalAlign: "middle" }} />
                                        <strong>Phone:</strong> {order.deliveryInfo?.phoneNumber || order.customerPhone || 'N/A'}
                                    </Typography>
                                    {(order.deliveryInfo?.mail || order.customerEmail) && (
                                        <Typography variant="body2">
                                            <strong>Email:</strong> {order.deliveryInfo?.mail || order.customerEmail}
                                        </Typography>
                                    )}
                                    <Typography variant="body2">
                                        <LocationOn sx={{ mr: 1, fontSize: 16, verticalAlign: "middle" }} />
                                        <strong>Address:</strong> {
                                            order.deliveryInfo?.addressDetail || 
                                            `${order.deliveryInfo?.city || ''} ${order.deliveryInfo?.district || ''}`.trim() ||
                                            'N/A'
                                        }
                                    </Typography>
                                    {order.deliveryInfo?.city && order.deliveryInfo?.district && (
                                        <Typography variant="body2">
                                            <strong>City/District:</strong> {order.deliveryInfo.city}, {order.deliveryInfo.district}
                                        </Typography>
                                    )}
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
                                                    <Avatar sx={{ width: 56, height: 56, bgcolor: "primary.main" }}>
                                                        {(item.productTitle || item.title || 'P').charAt(0).toUpperCase()}
                                                    </Avatar>
                                                </ListItemAvatar>
                                                <ListItemText
                                                    primary={
                                                        <Typography variant="body1">
                                                            {item.productTitle || item.title || 'Product'}
                                                        </Typography>
                                                    }
                                                    secondary={
                                                        <Box>
                                                            <Typography variant="body2" color="text.secondary">
                                                                {formatPrice(item.productPrice || item.price || 0)} × {item.quantity}
                                                            </Typography>
                                                            <Typography variant="body2" color="text.secondary">
                                                                Product ID: {item.productID}
                                                            </Typography>
                                                        </Box>
                                                    }
                                                />
                                                <Typography variant="body1" sx={{ fontWeight: "bold" }}>
                                                    {formatPrice((item.productPrice || item.price || 0) * item.quantity)}
                                                </Typography>
                                            </ListItem>
                                        ))}
                                    </List>
                                ) : (
                                    <Typography variant="body2" color="text.secondary">
                                        Order items information not available. Total amount: {formatPrice(order.totalPrice || order.totalAmount || 0)}
                                    </Typography>
                                )}

                                <Divider sx={{ my: 2 }} />

                                {/* Order Totals */}
                                <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                                    {order?.items && order.items.length > 0 ? (
                                        <>
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
                                        </>
                                    ) : null}
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

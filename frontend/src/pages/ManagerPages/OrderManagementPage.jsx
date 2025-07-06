import React, { useState, useEffect } from "react";
import {
    Container,
    Typography,
    Box,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    IconButton,
    Card,
    CardContent,
    Grid,
    Chip,
    Tooltip,
    Alert,
    Snackbar,
    Pagination,
    Tabs,
    Tab,
    Badge,
} from "@mui/material";
import {
    Visibility,
    Receipt,
    LocalShipping,
    Warning,
    CheckCircle,
    ThumbUp,
    ThumbDown,
    Pending,
    Block,
    HourglassEmpty,
    Cancel,
} from "@mui/icons-material";
import OrderDialog from "../../components/Manager/OrderDialog";
import RejectOrderDialog from "../../components/Manager/RejectOrderDialog";
import LoadingSpinner from "../../components/Common/LoadingSpinner";
import { orderService } from '../../services/orderService';
import { transformOrdersArray, transformOrderData } from '../../utils/orderDataTransformer';

const OrderManagementPage = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentTab, setCurrentTab] = useState(0);

    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(10);

    // Dialog states
    const [orderDialog, setOrderDialog] = useState({ open: false, order: null, mode: "view" });
    const [rejectDialog, setRejectDialog] = useState({ open: false, orderId: null });
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    useEffect(() => {
        loadOrders();
    }, []);

    const loadOrders = async () => {
        try {
            setLoading(true);
            const rawData = await orderService.getAllOrders();
            console.log("🔍 DEBUG - Raw API data:", rawData);
            
            // Transform data to match UI expectations
            const transformedData = transformOrdersArray(rawData);
            console.log("🔍 DEBUG - Transformed data:", transformedData);
            
            setOrders(transformedData);
        } catch (error) {
            console.error('Error loading orders:', error);
            setSnackbar({
                open: true,
                message: 'Error loading orders',
                severity: 'error'
            });
        } finally {
            setLoading(false);
        }
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat("en-US", {
            style: "currency",
            currency: "USD",
        }).format(price);
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString("en-US");
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

    const getStatusIcon = (status) => {
        switch (status) {
            case "PENDING":
                return <Warning />;
            case "APPROVED":
                return <CheckCircle />;
            case "REJECTED":
            case "CANCELLED":
                return <Cancel />;
            default:
                return <Receipt />;
        }
    };

    // Filter orders by tab
    const getFilteredOrders = () => {
        let filtered = orders;

        // Filter by tab
        switch (currentTab) {
            case 0: // Pending Orders
                filtered = filtered.filter((order) => order.status === "PENDING");
                break;
            case 1: // Processing Orders
                filtered = filtered.filter((order) => order.status === "APPROVED");
                break;
            case 2: // Rejected Orders
                filtered = filtered.filter((order) => order.status === "CANCELLED" || order.status === "REJECTED");
                break;
            default:
                break;
        }

        return filtered;
    };

    const filteredOrders = getFilteredOrders();

    const handleView = (order) => {
        // Ensure data is properly transformed
        const transformedOrder = transformOrderData(order);
        
        console.log("🔍 DEBUG - Original order:", order);
        console.log("🔍 DEBUG - Transformed order:", transformedOrder);
        console.log("🔍 DEBUG - Order structure:", {
            id: transformedOrder.id,
            orderID: transformedOrder.orderID,
            status: transformedOrder.status,
            totalPrice: transformedOrder.totalPrice,
            totalAmount: transformedOrder.totalAmount,
            customerName: transformedOrder.customerName,
            customerPhone: transformedOrder.customerPhone,
            deliveryInfo: transformedOrder.deliveryInfo,
            items: transformedOrder.items
        });
        
        setOrderDialog({ open: true, order: transformedOrder, mode: "view" });
    };

    const handleApprove = async (orderId) => {
        try {
            const result = await orderService.approveOrder(orderId, "manager");
            setSnackbar({
                open: true,
                message: result.message || 'Order approved successfully',
                severity: 'success'
            });
            loadOrders(); // Reload orders
        } catch (error) {
            console.error('Error approving order:', error);
            setSnackbar({
                open: true,
                message: error.message || 'Error approving order',
                severity: 'error'
            });
        }
    };

    const handleReject = async (orderId, reason) => {
        try {
            const result = await orderService.rejectOrder(orderId, reason, "manager");
            setSnackbar({
                open: true,
                message: result.message || 'Order rejected successfully',
                severity: 'success'
            });
            loadOrders(); // Reload orders
        } catch (error) {
            console.error('Error rejecting order:', error);
            setSnackbar({
                open: true,
                message: error.message || 'Error rejecting order',
                severity: 'error'
            });
        }
    };

    const openRejectDialog = (orderId) => {
        setRejectDialog({ open: true, orderId });
    };

    const closeRejectDialog = () => {
        setRejectDialog({ open: false, orderId: null });
    };

    const handleSave = async (updatedOrder) => {
        try {
            console.log("Updating order:", updatedOrder);
            setSnackbar({
                open: true,
                message: "Order updated successfully",
                severity: "success",
            });

            setOrderDialog({ open: false, order: null, mode: "view" });
            // In real app, reload orders from API
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Failed to update order",
                severity: "error",
            });
        }
    };



    const handleTabChange = (event, newValue) => {
        setCurrentTab(newValue);
        setCurrentPage(1); // Reset pagination when changing tabs
    };

    // Pagination
    const totalPages = Math.ceil(filteredOrders.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const paginatedOrders = filteredOrders.slice(startIndex, startIndex + itemsPerPage);

    // Statistics for each tab
    const orderStats = {
        pending: orders.filter((o) => o.status === "PENDING").length,
        processing: orders.filter((o) => o.status === "APPROVED").length,
        rejected: orders.filter((o) => o.status === "CANCELLED" || o.status === "REJECTED").length,
        total: orders.length,
    };

    const renderOrderTable = () => {
        return (
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Order ID</TableCell>
                            <TableCell>Customer</TableCell>
                            <TableCell>Total</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Type</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {paginatedOrders.map((order) => (
                            <TableRow key={order.id || order.orderID} hover>
                                <TableCell>
                                    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                                        <Receipt color="primary" />
                                        <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                            {order.id || order.orderID || 'N/A'}
                                        </Typography>
                                    </Box>
                                </TableCell>
                                <TableCell>
                                    <Box>
                                        <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                            {order.deliveryInfo?.recipientName || order.customerName || 'N/A'}
                                        </Typography>
                                        <Typography variant="caption" color="text.secondary">
                                            {order.deliveryInfo?.phoneNumber || order.customerPhone || 'N/A'}
                                        </Typography>
                                    </Box>
                                </TableCell>
                                <TableCell>
                                    <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                        {formatPrice(order.totalPrice || order.totalAmount || 0)}
                                    </Typography>
                                    <Typography variant="caption" color="text.secondary">
                                        {order.items?.length || 0} item{(order.items?.length || 0) !== 1 ? "s" : ""}
                                    </Typography>
                                </TableCell>
                                <TableCell>
                                    <Chip
                                        icon={getStatusIcon(order.status)}
                                        label={getStatusDisplayName(order.status)}
                                        color={getStatusColor(order.status)}
                                        size="small"
                                    />
                                </TableCell>
                                <TableCell>
                                    <Chip
                                        label={order.isRushOrder ? "Rush" : "Standard"}
                                        color={order.isRushOrder ? "error" : "default"}
                                        size="small"
                                        variant="outlined"
                                    />
                                </TableCell>
                                <TableCell>
                                    <Box sx={{ display: "flex", gap: 1 }}>
                                        <Tooltip title="View Details">
                                            <IconButton size="small" onClick={() => handleView(order)}>
                                                <Visibility />
                                            </IconButton>
                                        </Tooltip>

                                        {/* Actions based on tab */}
                                        {currentTab === 0 && ( // Pending Orders
                                            <>
                                                <Tooltip title="Approve Order">
                                                    <IconButton size="small" color="success" onClick={() => handleApprove(order.id || order.orderID)}>
                                                        <ThumbUp />
                                                    </IconButton>
                                                </Tooltip>
                                                <Tooltip title="Reject Order">
                                                    <IconButton size="small" color="error" onClick={() => openRejectDialog(order.id || order.orderID)}>
                                                        <ThumbDown />
                                                    </IconButton>
                                                </Tooltip>
                                            </>
                                        )}
                                    </Box>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        );
    };

    if (loading) {
        return <LoadingSpinner message="Loading orders..." />;
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            {/* Header */}
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Order Management
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Manage and approve customer orders
                </Typography>
            </Box>

            {/* Statistics Cards */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={6} md={3}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="primary">
                                {orderStats.total}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Total Orders
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="warning.main">
                                {orderStats.pending}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Pending Orders
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="info.main">
                                {orderStats.processing}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Approved Orders
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="error.main">
                                {orderStats.rejected}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Rejected Orders
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Tabs */}
            <Card sx={{ mb: 3 }}>
                <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                    <Tabs value={currentTab} onChange={handleTabChange} aria-label="order management tabs">
                        <Tab
                            icon={
                                <Badge badgeContent={orderStats.pending} color="warning">
                                    <Pending />
                                </Badge>
                            }
                            label="Pending Orders"
                            sx={{ minHeight: 72 }}
                        />
                        <Tab
                            icon={
                                <Badge badgeContent={orderStats.processing} color="info">
                                    <HourglassEmpty />
                                </Badge>
                            }
                                                            label="Approved Orders"
                            sx={{ minHeight: 72 }}
                        />
                        <Tab
                            icon={
                                <Badge badgeContent={orderStats.rejected} color="error">
                                    <Block />
                                </Badge>
                            }
                            label="Rejected Orders"
                            sx={{ minHeight: 72 }}
                        />
                    </Tabs>
                </Box>


            </Card>

            {/* Orders Table */}
            {renderOrderTable()}

            {/* Pagination */}
            {totalPages > 1 && (
                <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
                    <Pagination count={totalPages} page={currentPage} onChange={(e, page) => setCurrentPage(page)} color="primary" />
                </Box>
            )}



            {/* Order Dialog */}
            <OrderDialog
                open={orderDialog.open}
                onClose={() => setOrderDialog({ open: false, order: null, mode: "view" })}
                order={orderDialog.order}
                mode={orderDialog.mode}
                onSave={handleSave}
            />

            {/* Reject Order Dialog */}
            <RejectOrderDialog
                open={rejectDialog.open}
                onClose={closeRejectDialog}
                onReject={handleReject}
                orderId={rejectDialog.orderId}
            />

            {/* Snackbar */}
            <Snackbar
                open={snackbar.open}
                autoHideDuration={3000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
                anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            >
                <Alert onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Container>
    );
};

export default OrderManagementPage;

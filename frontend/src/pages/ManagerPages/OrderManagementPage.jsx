import React, { useState, useEffect } from "react";
import {
    Container,
    Typography,
    Box,
    Button,
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
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Chip,
    Tooltip,
    Alert,
    Snackbar,
    Pagination,
    InputAdornment,
    Tabs,
    Tab,
    Badge,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
} from "@mui/material";
import {
    Visibility,
    Search,
    FilterList,
    Receipt,
    LocalShipping,
    Warning,
    CheckCircle,
    Download,
    ThumbUp,
    ThumbDown,
    Cancel,
    Save,
    Pending,
    Block,
    HourglassEmpty,
    Refresh,
} from "@mui/icons-material";
import OrderDialog from "../../components/Manager/OrderDialog";
import RejectOrderDialog from "../../components/Manager/RejectOrderDialog";
import LoadingSpinner from "../../components/Common/LoadingSpinner";
import { orderService } from '../../services/orderService';

const OrderManagementPage = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentTab, setCurrentTab] = useState(0);
    const [searchQuery, setSearchQuery] = useState("");
    const [rushFilter, setRushFilter] = useState("");
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
            const data = await orderService.getAllOrders();
            console.log("data", data);
            setOrders(data);
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

    const getStatusIcon = (status) => {
        switch (status) {
            case "PENDING_APPROVAL":
                return <Warning />;
            case "PROCESSING":
                return <Receipt />;
            case "SHIPPING":
                return <LocalShipping />;
            case "DELIVERED":
                return <CheckCircle />;
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
                filtered = filtered.filter((order) => order.status === "PENDING_APPROVAL");
                break;
            case 1: // Processing Orders
                filtered = filtered.filter((order) => ["PROCESSING", "SHIPPING", "DELIVERED"].includes(order.status));
                break;
            case 2: // Rejected Orders
                filtered = filtered.filter((order) => order.status === "CANCELLED");
                break;
            default:
                break;
        }

        // Apply search filter
        if (searchQuery) {
            filtered = filtered.filter(
                (order) =>
                    order.orderID.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    order.customerName.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    order.customerPhone.includes(searchQuery)
            );
        }

        // Apply rush filter
        if (rushFilter) {
            const isRush = rushFilter === "rush";
            filtered = filtered.filter((order) => order.isRushOrder === isRush);
        }

        return filtered;
    };

    const filteredOrders = getFilteredOrders();

    const handleView = (order) => {
        setOrderDialog({ open: true, order, mode: "view" });
    };

    const handleApprove = async (orderId) => {
        try {
            await orderService.approveOrder(orderId);
            setSnackbar({
                open: true,
                message: 'Order approved successfully',
                severity: 'success'
            });
            loadOrders(); // Reload orders
        } catch (error) {
            console.error('Error approving order:', error);
            setSnackbar({
                open: true,
                message: 'Error approving order',
                severity: 'error'
            });
        }
    };

    const handleReject = async (orderId, reason) => {
        try {
            await orderService.rejectOrder(orderId, reason);
            setSnackbar({
                open: true,
                message: 'Order rejected successfully',
                severity: 'success'
            });
            loadOrders(); // Reload orders
        } catch (error) {
            console.error('Error rejecting order:', error);
            setSnackbar({
                open: true,
                message: 'Error rejecting order',
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

    const handleExport = () => {
        // In real app, this would export orders to CSV/Excel
        console.log("Exporting orders");
        setSnackbar({
            open: true,
            message: "Export feature coming soon",
            severity: "info",
        });
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
        pending: orders.filter((o) => o.status === "PENDING_APPROVAL").length,
        processing: orders.filter((o) => ["PROCESSING", "SHIPPING", "DELIVERED"].includes(o.status)).length,
        rejected: orders.filter((o) => o.status === "CANCELLED").length,
        total: orders.length,
        totalRevenue: orders.reduce((sum, order) => sum + order.totalAmount, 0),
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
                            <TableCell>Date</TableCell>
                            <TableCell>Type</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {paginatedOrders.map((order) => (
                            <TableRow key={order.orderID} hover>
                                <TableCell>
                                    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                                        <Receipt color="primary" />
                                        <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                            {order.orderID}
                                        </Typography>
                                    </Box>
                                </TableCell>
                                <TableCell>
                                    <Box>
                                        <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                            {order.customerName}
                                        </Typography>
                                        <Typography variant="caption" color="text.secondary">
                                            {order.customerPhone}
                                        </Typography>
                                    </Box>
                                </TableCell>
                                <TableCell>
                                    <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                        {formatPrice(order.totalAmount)}
                                    </Typography>
                                    <Typography variant="caption" color="text.secondary">
                                        {order.items?.length} item{order.items?.length !== 1 ? "s" : ""}
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
                                    <Typography variant="body2">{formatDate(order.orderDate)}</Typography>
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
                                                    <IconButton size="small" color="success" onClick={() => handleApprove(order.orderID)}>
                                                        <ThumbUp />
                                                    </IconButton>
                                                </Tooltip>
                                                <Tooltip title="Reject Order">
                                                    <IconButton size="small" color="error" onClick={() => openRejectDialog(order.orderID)}>
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
                                Processing Orders
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
                            label="Processing Orders"
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

                {/* Controls */}
                <CardContent>
                    <Grid container spacing={2} alignItems="center">
                        <Grid item xs={12} sm={4}>
                            <TextField
                                fullWidth
                                placeholder="Search orders..."
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                InputProps={{
                                    startAdornment: (
                                        <InputAdornment position="start">
                                            <Search />
                                        </InputAdornment>
                                    ),
                                }}
                            />
                        </Grid>
                        <Grid item xs={12} sm={3}>
                            <FormControl fullWidth>
                                <InputLabel>Delivery Type</InputLabel>
                                <Select value={rushFilter} onChange={(e) => setRushFilter(e.target.value)} label="Delivery Type">
                                    <MenuItem value="">All Types</MenuItem>
                                    <MenuItem value="rush">Rush Orders</MenuItem>
                                    <MenuItem value="standard">Standard Orders</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={3}>
                            <Button variant="outlined" startIcon={<Download />} onClick={handleExport} fullWidth>
                                Export
                            </Button>
                        </Grid>
                        <Grid item xs={12} sm={2}>
                            <Typography variant="body1" align="center">
                                {filteredOrders.length} order{filteredOrders.length !== 1 ? "s" : ""}
                            </Typography>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>

            {/* Orders Table */}
            {renderOrderTable()}

            {/* Pagination */}
            {totalPages > 1 && (
                <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
                    <Pagination count={totalPages} page={currentPage} onChange={(e, page) => setCurrentPage(page)} color="primary" />
                </Box>
            )}

            {/* Revenue Summary */}
            <Card sx={{ mt: 4 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom>
                        Revenue Summary
                    </Typography>
                    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                        <Typography variant="body1">Total Revenue from {orders.length} orders:</Typography>
                        <Typography variant="h5" color="success.main" sx={{ fontWeight: "bold" }}>
                            {formatPrice(orderStats.totalRevenue)}
                        </Typography>
                    </Box>
                </CardContent>
            </Card>

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

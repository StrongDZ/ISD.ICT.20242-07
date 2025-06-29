import React, { useState, useEffect } from "react";
import {
    Container,
    Typography,
    Grid,
    Card,
    CardContent,
    Box,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Chip,
    IconButton,
    Snackbar,
    Alert,
    CircularProgress,
    Skeleton,
} from "@mui/material";
import { Inventory, ShoppingCart, TrendingUp, Add, Edit, Visibility, Assignment } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { productService } from "../../services/productService";
import { orderService } from "../../services/orderService";
import ProductDetailDialog from "../../components/Product/ProductDetailDialog";
import ProductEditDialog from "../../components/Product/ProductEditDialog";

const ManagerDashboard = () => {
    const navigate = useNavigate();
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [stats, setStats] = useState({
        totalProducts: 0,
        lowStockProducts: 0,
        pendingOrders: 0,
    });

    // Dialog states
    const [detailDialog, setDetailDialog] = useState({ open: false, product: null });
    const [editDialog, setEditDialog] = useState({ open: false, product: null, mode: "view" });
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            setLoading(true);
            setError(null);

            // Load products and orders from API concurrently
            const [productsResponse, ordersResponse] = await Promise.all([
                productService.getAllProductsNoPagination(),
                orderService.getAllOrders()
            ]);

            // Handle products data from API
            const productsData = Array.isArray(productsResponse) ? productsResponse : productsResponse.content || [];
            setProducts(productsData.slice(0, 10)); // Show recent 10 products

            // Handle orders data from API
            const ordersData = Array.isArray(ordersResponse) ? ordersResponse : ordersResponse.content || [];
            setOrders(ordersData.slice(0, 5)); // Show recent 5 orders

            // Calculate statistics
            const lowStockCount = productsData.filter((p) => (p.quantity || 0) <= 5).length;
            const pendingOrdersCount = ordersData.filter((o) => 
                o.status === "PENDING_APPROVAL" || o.status === "PENDING"
            ).length;

            setStats({
                totalProducts: productsData.length,
                lowStockProducts: lowStockCount,
                pendingOrders: pendingOrdersCount,
            });

        } catch (error) {
            console.error("Error loading dashboard data:", error);
            setError("Failed to load dashboard data. Please try again.");
            
            // Fallback to mock data if API fails
            const mockProducts = productService.getMockProducts();
            const mockOrders = orderService.getMockOrders();
            
            setProducts(mockProducts.slice(0, 10));
            setOrders(mockOrders.slice(0, 5));
            
            const lowStockCount = mockProducts.filter((p) => p.quantity <= 5).length;
            const pendingOrdersCount = mockOrders.filter((o) => o.status === "PENDING_APPROVAL").length;

            setStats({
                totalProducts: mockProducts.length,
                lowStockProducts: lowStockCount,
                pendingOrders: pendingOrdersCount,
            });
        } finally {
            setLoading(false);
        }
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price || 0);
    };

    const getStatusColor = (status) => {
        switch (status) {
            case "PENDING_APPROVAL":
            case "PENDING":
                return "warning";
            case "PROCESSING":
                return "info";
            case "SHIPPING":
                return "primary";
            case "DELIVERED":
            case "COMPLETED":
                return "success";
            case "REJECTED":
            case "CANCELLED":
                return "error";
            default:
                return "default";
        }
    };

    const getStatusText = (status) => {
        switch (status) {
            case "PENDING_APPROVAL":
                return "PENDING APPROVAL";
            case "PENDING":
                return "PENDING";
            case "PROCESSING":
                return "PROCESSING";
            case "SHIPPING":
                return "SHIPPING";
            case "DELIVERED":
                return "DELIVERED";
            case "COMPLETED":
                return "COMPLETED";
            case "REJECTED":
                return "REJECTED";
            case "CANCELLED":
                return "CANCELLED";
            default:
                return status || "UNKNOWN";
        }
    };

    const handleViewProduct = (product) => {
        setDetailDialog({ open: true, product });
    };

    const handleEditProduct = (product) => {
        setEditDialog({ open: true, product: { ...product }, mode: "edit" });
    };

    const handleEditFromDetail = (product) => {
        setDetailDialog({ open: false, product: null });
        setEditDialog({ open: true, product: { ...product }, mode: "edit" });
    };

    const handleSaveProduct = async (productData, mode) => {
        try {
            if (mode === "add") {
                await productService.createProduct(productData);
            } else {
                await productService.updateProduct(productData.productID, productData);
            }

            setSnackbar({
                open: true,
                message: `Product ${mode === "add" ? "added" : "updated"} successfully`,
                severity: "success",
            });

            setEditDialog({ open: false, product: null, mode: "view" });
            // Reload dashboard data to reflect changes
            loadDashboardData();
        } catch (error) {
            console.error("Error saving product:", error);
            setSnackbar({
                open: true,
                message: error.message || "Failed to save product",
                severity: "error",
            });
        }
    };

    const handleAddNewProduct = () => {
        setEditDialog({ open: true, product: null, mode: "add" });
    };

    const StatCard = ({ title, value, icon, color = "primary", action, loading = false }) => (
        <Card sx={{ height: "100%" }}>
            <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <Box>
                        <Typography color="textSecondary" gutterBottom variant="h6">
                            {title}
                        </Typography>
                        {loading ? (
                            <Skeleton width={80} height={40} />
                        ) : (
                            <Typography variant="h4" component="h2" color={color}>
                                {value}
                            </Typography>
                        )}
                    </Box>
                    <Box sx={{ color: `${color}.main` }}>{icon}</Box>
                </Box>
                {action && <Box sx={{ mt: 2 }}>{action}</Box>}
            </CardContent>
        </Card>
    );

    if (error) {
        return (
            <Container maxWidth="lg" sx={{ py: 4 }}>
                <Alert severity="warning" sx={{ mb: 2 }}>
                    {error} Using fallback data.
                </Alert>
                {/* Continue rendering with fallback data */}
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Manager Dashboard
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Welcome back! Here's an overview of your store management.
                </Typography>
            </Box>

            {error && (
                <Alert severity="warning" sx={{ mb: 2 }}>
                    {error} Using fallback data.
                </Alert>
            )}



            {/* Statistics Cards */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={6} md={3}>
                    <StatCard
                        title="Total Products"
                        value={stats.totalProducts}
                        icon={<Inventory sx={{ fontSize: 40 }} />}
                        color="primary"
                        loading={loading}
                        action={
                            <Button 
                                variant="outlined" 
                                size="small" 
                                startIcon={<Add />} 
                                onClick={handleAddNewProduct}
                                disabled={loading}
                            >
                                Add Product
                            </Button>
                        }
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <StatCard
                        title="Low Stock Items"
                        value={stats.lowStockProducts}
                        icon={<TrendingUp sx={{ fontSize: 40 }} />}
                        color="warning"
                        loading={loading}
                        action={
                            <Button 
                                variant="outlined" 
                                size="small" 
                                onClick={() => navigate("/manager/products")}
                                disabled={loading}
                            >
                                Manage Stock
                            </Button>
                        }
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <StatCard
                        title="Pending Orders"
                        value={stats.pendingOrders}
                        icon={<ShoppingCart sx={{ fontSize: 40 }} />}
                        color="info"
                        loading={loading}
                        action={
                            <Button 
                                variant="outlined" 
                                size="small" 
                                onClick={() => navigate("/manager/orders")}
                                disabled={loading}
                            >
                                Review Orders
                            </Button>
                        }
                    />
                </Grid>
            </Grid>

            {/* Quick Actions */}
            <Box sx={{ mb: 4 }}>
                <Typography variant="h5" gutterBottom>
                    Quick Actions
                </Typography>
                <Grid container spacing={2}>
                    <Grid item>
                        <Button 
                            variant="contained" 
                            startIcon={<Add />} 
                            onClick={handleAddNewProduct}
                            disabled={loading}
                        >
                            Add New Product
                        </Button>
                    </Grid>
                    <Grid item>
                        <Button 
                            variant="outlined" 
                            startIcon={<Assignment />} 
                            onClick={() => navigate("/manager/orders")}
                            disabled={loading}
                        >
                            Manage Orders
                        </Button>
                    </Grid>
                    <Grid item>
                        <Button 
                            variant="outlined" 
                            startIcon={<Inventory />} 
                            onClick={() => navigate("/manager/products")}
                            disabled={loading}
                        >
                            Update Inventory
                        </Button>
                    </Grid>
                </Grid>
            </Box>

            {/* Recent Products and Orders */}
            <Grid container spacing={4}>
                {/* Recent Products */}
                <Grid item xs={12} lg={8}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
                                <Typography variant="h6">Recent Products</Typography>
                                <Button 
                                    size="small" 
                                    onClick={() => navigate("/manager/products")}
                                    disabled={loading}
                                >
                                    View All Products
                                </Button>
                            </Box>
                            {loading ? (
                                <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
                                    <CircularProgress />
                                </Box>
                            ) : (
                                <TableContainer>
                                    <Table>
                                        <TableHead>
                                            <TableRow>
                                                <TableCell>Product</TableCell>
                                                <TableCell>Category</TableCell>
                                                <TableCell align="right">Stock</TableCell>
                                                <TableCell align="right">Price</TableCell>
                                                <TableCell align="center">Actions</TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {products.map((product) => (
                                                <TableRow key={product.productID}>
                                                    <TableCell>
                                                        <Typography variant="subtitle2" noWrap>
                                                            {product.title}
                                                        </Typography>
                                                    </TableCell>
                                                    <TableCell>
                                                        <Chip 
                                                            label={product.category?.toUpperCase() || 'N/A'} 
                                                            size="small" 
                                                            variant="outlined"
                                                        />
                                                    </TableCell>
                                                    <TableCell align="right">
                                                        <Typography 
                                                            color={(product.quantity || 0) <= 5 ? "error" : "textPrimary"}
                                                            fontWeight={(product.quantity || 0) <= 5 ? "bold" : "normal"}
                                                        >
                                                            {product.quantity || 0}
                                                        </Typography>
                                                    </TableCell>
                                                    <TableCell align="right">
                                                        {formatPrice(product.price)}
                                                    </TableCell>
                                                    <TableCell align="center">
                                                        <IconButton 
                                                            size="small" 
                                                            onClick={() => handleViewProduct(product)}
                                                            title="View Details"
                                                        >
                                                            <Visibility />
                                                        </IconButton>
                                                        <IconButton 
                                                            size="small" 
                                                            onClick={() => handleEditProduct(product)}
                                                            title="Edit Product"
                                                        >
                                                            <Edit />
                                                        </IconButton>
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                            {products.length === 0 && (
                                                <TableRow>
                                                    <TableCell colSpan={5} align="center">
                                                        <Typography color="textSecondary">
                                                            No products found
                                                        </Typography>
                                                    </TableCell>
                                                </TableRow>
                                            )}
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            )}
                        </CardContent>
                    </Card>
                </Grid>

                {/* Recent Orders */}
                <Grid item xs={12} lg={4}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
                                <Typography variant="h6">Recent Orders</Typography>
                                <Button 
                                    size="small" 
                                    onClick={() => navigate("/manager/orders")}
                                    disabled={loading}
                                >
                                    View All Orders
                                </Button>
                            </Box>
                            {loading ? (
                                <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
                                    <CircularProgress />
                                </Box>
                            ) : (
                                <Box>
                                    {orders.map((order) => (
                                        <Box key={order.id || order.orderID} sx={{ mb: 2, p: 2, border: 1, borderColor: "divider", borderRadius: 1 }}>
                                            <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", mb: 1 }}>
                                                <Typography variant="subtitle2" fontWeight="bold">
                                                    {order.id || order.orderID || 'N/A'}
                                                </Typography>
                                                <Chip 
                                                    label={getStatusText(order.status)} 
                                                    size="small" 
                                                    color={getStatusColor(order.status)}
                                                />
                                            </Box>
                                            <Typography variant="body2" color="textSecondary" gutterBottom>
                                                Customer: {order.deliveryInfo?.recipientName || order.customerName || order.customer?.name || 'N/A'}
                                            </Typography>
                                            <Typography variant="body2" fontWeight="bold" color="primary">
                                                Total: {formatPrice(order.totalPrice || order.totalAmount || order.total || order.amount || 0)}
                                            </Typography>
                                            <Typography variant="caption" color="textSecondary">
                                                Status: {order.status || 'N/A'}
                                            </Typography>
                                        </Box>
                                    ))}
                                    {orders.length === 0 && (
                                        <Typography color="textSecondary" align="center">
                                            No recent orders
                                        </Typography>
                                    )}
                                </Box>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Dialogs */}
            <ProductDetailDialog
                open={detailDialog.open}
                onClose={() => setDetailDialog({ open: false, product: null })}
                product={detailDialog.product}
                onEdit={handleEditFromDetail}
            />

            <ProductEditDialog
                open={editDialog.open}
                onClose={() => setEditDialog({ open: false, product: null, mode: "view" })}
                productData={editDialog.product}
                mode={editDialog.mode}
                onSave={handleSaveProduct}
            />

            {/* Snackbar */}
            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
            >
                <Alert onClose={() => setSnackbar({ ...snackbar, open: false })} severity={snackbar.severity}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Container>
    );
};

export default ManagerDashboard;

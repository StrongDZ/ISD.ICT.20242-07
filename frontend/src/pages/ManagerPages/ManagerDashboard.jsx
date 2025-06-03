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
} from "@mui/material";
import { Inventory, ShoppingCart, TrendingUp, Add, Edit, Visibility, Assignment } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { productService } from "../../services/productService";
import { cartService } from "../../services/cartService";
import ProductDetailDialog from "../../components/Product/ProductDetailDialog";
import ProductEditDialog from "../../components/Product/ProductEditDialog";

const ManagerDashboard = () => {
    const navigate = useNavigate();
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);
    const [stats, setStats] = useState({
        totalProducts: 0,
        lowStockProducts: 0,
        pendingOrders: 0,
        totalRevenue: 0,
    });

    // Dialog states
    const [detailDialog, setDetailDialog] = useState({ open: false, product: null });
    const [editDialog, setEditDialog] = useState({ open: false, product: null, mode: "view" });
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = () => {
        // Load products
        const mockProducts = productService.getMockProducts();
        setProducts(mockProducts.slice(0, 10)); // Show recent 10 products

        // Load orders
        const mockOrders = cartService.getMockOrders();
        setOrders(mockOrders.slice(0, 5)); // Show recent 5 orders

        // Calculate stats
        const lowStockCount = mockProducts.filter((p) => p.quantity <= 5).length;
        const pendingOrdersCount = mockOrders.filter((o) => o.status === "PENDING_APPROVAL").length;
        const totalRevenue = mockOrders.reduce((sum, order) => sum + order.totalAmount, 0);

        setStats({
            totalProducts: mockProducts.length,
            lowStockProducts: lowStockCount,
            pendingOrders: pendingOrdersCount,
            totalRevenue: totalRevenue,
        });
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
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
            default:
                return "default";
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
            // In real app, this would call the API
            console.log(`${mode === "add" ? "Adding" : "Updating"} product:`, productData);

            setSnackbar({
                open: true,
                message: `Product ${mode === "add" ? "added" : "updated"} successfully`,
                severity: "success",
            });

            setEditDialog({ open: false, product: null, mode: "view" });
            // In real app, reload dashboard data
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Failed to save product",
                severity: "error",
            });
        }
    };

    const StatCard = ({ title, value, icon, color = "primary", action }) => (
        <Card sx={{ height: "100%" }}>
            <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <Box>
                        <Typography color="textSecondary" gutterBottom variant="h6">
                            {title}
                        </Typography>
                        <Typography variant="h4" component="h2" color={color}>
                            {value}
                        </Typography>
                    </Box>
                    <Box sx={{ color: `${color}.main` }}>{icon}</Box>
                </Box>
                {action && <Box sx={{ mt: 2 }}>{action}</Box>}
            </CardContent>
        </Card>
    );

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

            {/* Statistics Cards */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={6} md={3}>
                    <StatCard
                        title="Total Products"
                        value={stats.totalProducts}
                        icon={<Inventory sx={{ fontSize: 40 }} />}
                        color="primary"
                        action={
                            <Button variant="outlined" size="small" startIcon={<Add />} onClick={() => navigate("/manager/products")}>
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
                        action={
                            <Button variant="outlined" size="small" onClick={() => navigate("/manager/products")}>
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
                        action={
                            <Button variant="outlined" size="small" onClick={() => navigate("/manager/orders")}>
                                Review Orders
                            </Button>
                        }
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <StatCard
                        title="Total Revenue"
                        value={formatPrice(stats.totalRevenue)}
                        icon={<Assignment sx={{ fontSize: 40 }} />}
                        color="success"
                    />
                </Grid>
            </Grid>

            {/* Quick Actions */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Quick Actions
                            </Typography>
                            <Box sx={{ display: "flex", gap: 2, flexWrap: "wrap" }}>
                                <Button variant="contained" startIcon={<Add />} onClick={() => navigate("/manager/products")}>
                                    Add New Product
                                </Button>
                                <Button variant="outlined" startIcon={<Assignment />} onClick={() => navigate("/manager/orders")}>
                                    Manage Orders
                                </Button>
                                <Button variant="outlined" startIcon={<Inventory />} onClick={() => navigate("/manager/products")}>
                                    Update Inventory
                                </Button>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Recent Products and Orders */}
            <Grid container spacing={3}>
                {/* Recent Products */}
                <Grid item xs={12} md={7}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Recent Products
                            </Typography>
                            <TableContainer>
                                <Table size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Product</TableCell>
                                            <TableCell>Category</TableCell>
                                            <TableCell>Stock</TableCell>
                                            <TableCell>Price</TableCell>
                                            <TableCell>Actions</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {products.slice(0, 5).map((product) => (
                                            <TableRow key={product.productID}>
                                                <TableCell>
                                                    <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                                        {product.title.length > 30 ? product.title.substring(0, 30) + "..." : product.title}
                                                    </Typography>
                                                </TableCell>
                                                <TableCell>
                                                    <Chip label={product.category.toUpperCase()} size="small" color="primary" />
                                                </TableCell>
                                                <TableCell>
                                                    <Typography
                                                        color={product.quantity <= 5 ? "error" : "textPrimary"}
                                                        sx={{ fontWeight: product.quantity <= 5 ? "bold" : "normal" }}
                                                    >
                                                        {product.quantity}
                                                    </Typography>
                                                </TableCell>
                                                <TableCell>{formatPrice(product.price)}</TableCell>
                                                <TableCell>
                                                    <IconButton size="small" onClick={() => handleViewProduct(product)}>
                                                        <Visibility />
                                                    </IconButton>
                                                    <IconButton size="small" onClick={() => handleEditProduct(product)}>
                                                        <Edit />
                                                    </IconButton>
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                            <Box sx={{ mt: 2, textAlign: "right" }}>
                                <Button variant="outlined" onClick={() => navigate("/manager/products")}>
                                    View All Products
                                </Button>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Recent Orders */}
                <Grid item xs={12} md={5}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Recent Orders
                            </Typography>
                            <Box sx={{ maxHeight: 400, overflow: "auto" }}>
                                {orders.map((order) => (
                                    <Box
                                        key={order.orderID}
                                        sx={{
                                            p: 2,
                                            border: "1px solid",
                                            borderColor: "grey.200",
                                            borderRadius: 1,
                                            mb: 1,
                                        }}
                                    >
                                        <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 1 }}>
                                            <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                                {order.orderID}
                                            </Typography>
                                            <Chip label={order.status} size="small" color={getStatusColor(order.status)} />
                                        </Box>
                                        <Typography variant="body2" color="text.secondary">
                                            Customer: {order.customerName}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            Total: {formatPrice(order.totalAmount)}
                                        </Typography>
                                        <Typography variant="body2" color="text.secondary">
                                            Date: {new Date(order.orderDate).toLocaleDateString()}
                                        </Typography>
                                    </Box>
                                ))}
                            </Box>
                            <Box sx={{ mt: 2, textAlign: "right" }}>
                                <Button variant="outlined" onClick={() => navigate("/manager/orders")}>
                                    View All Orders
                                </Button>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Product Detail Dialog */}
            <ProductDetailDialog
                open={detailDialog.open}
                onClose={() => setDetailDialog({ open: false, product: null })}
                product={detailDialog.product}
                onEdit={handleEditFromDetail}
            />

            {/* Product Edit Dialog */}
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

export default ManagerDashboard;

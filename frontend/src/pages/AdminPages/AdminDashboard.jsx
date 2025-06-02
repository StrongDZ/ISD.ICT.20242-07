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
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
} from "@mui/material";
import {
    People,
    ShoppingCart,
    AttachMoney,
    Inventory,
    TrendingUp,
    PersonAdd,
    Settings,
    Visibility,
    Edit,
    Delete,
    CheckCircle,
    Warning,
    Info,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { authService } from "../../services/authService";
import { cartService } from "../../services/cartService";
import { productService } from "../../services/productService";

const AdminDashboard = () => {
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);
    const [orders, setOrders] = useState([]);
    const [products, setProducts] = useState([]);
    const [systemStats, setSystemStats] = useState({
        totalUsers: 0,
        totalOrders: 0,
        totalRevenue: 0,
        totalProducts: 0,
        activeUsers: 0,
        pendingOrders: 0,
    });

    useEffect(() => {
        loadAdminData();
    }, []);

    const loadAdminData = () => {
        // Load users
        const mockUsers = authService.getMockUsers();
        setUsers(mockUsers);

        // Load orders
        const mockOrders = cartService.getMockOrders();
        setOrders(mockOrders);

        // Load products
        const mockProducts = productService.getMockProducts();
        setProducts(mockProducts);

        // Calculate system statistics
        const totalRevenue = mockOrders.reduce((sum, order) => sum + order.totalAmount, 0);
        const pendingOrdersCount = mockOrders.filter((o) => o.status === "PENDING_APPROVAL").length;
        const activeUsersCount = mockUsers.filter((u) => u.isActive).length;

        setSystemStats({
            totalUsers: mockUsers.length,
            totalOrders: mockOrders.length,
            totalRevenue: totalRevenue,
            totalProducts: mockProducts.length,
            activeUsers: activeUsersCount,
            pendingOrders: pendingOrdersCount,
        });
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
        }).format(price);
    };

    const getRoleColor = (role) => {
        switch (role) {
            case "ADMINISTRATOR":
                return "error";
            case "PRODUCT_MANAGER":
                return "warning";
            case "CUSTOMER":
                return "info";
            default:
                return "default";
        }
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

    const StatCard = ({ title, value, icon, color = "primary", action, trend }) => (
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
                        {trend && (
                            <Typography variant="body2" color="success.main" sx={{ display: "flex", alignItems: "center", mt: 1 }}>
                                <TrendingUp sx={{ fontSize: 16, mr: 0.5 }} />
                                {trend}
                            </Typography>
                        )}
                    </Box>
                    <Box sx={{ color: `${color}.main` }}>{icon}</Box>
                </Box>
                {action && <Box sx={{ mt: 2 }}>{action}</Box>}
            </CardContent>
        </Card>
    );

    const recentActivities = [
        { icon: <PersonAdd />, text: "New user registered: jane_doe", time: "2 hours ago", type: "info" },
        { icon: <ShoppingCart />, text: "Order ORDER_005 requires approval", time: "3 hours ago", type: "warning" },
        { icon: <CheckCircle />, text: "Order ORDER_004 delivered successfully", time: "5 hours ago", type: "success" },
        { icon: <Warning />, text: "Low stock alert for 3 products", time: "6 hours ago", type: "warning" },
        { icon: <Info />, text: "System backup completed", time: "1 day ago", type: "info" },
    ];

    const getActivityColor = (type) => {
        switch (type) {
            case "success":
                return "success";
            case "warning":
                return "warning";
            case "error":
                return "error";
            default:
                return "info";
        }
    };

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    Admin Dashboard
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    System overview and management center.
                </Typography>
            </Box>

            {/* System Statistics */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={6} md={4}>
                    <StatCard
                        title="Total Users"
                        value={systemStats.totalUsers}
                        icon={<People sx={{ fontSize: 40 }} />}
                        color="primary"
                        trend="+5% this month"
                        action={
                            <Button variant="outlined" size="small" startIcon={<PersonAdd />} onClick={() => navigate("/admin/users")}>
                                Manage Users
                            </Button>
                        }
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <StatCard
                        title="Total Orders"
                        value={systemStats.totalOrders}
                        icon={<ShoppingCart sx={{ fontSize: 40 }} />}
                        color="info"
                        trend="+12% this week"
                        action={
                            <Button variant="outlined" size="small" onClick={() => navigate("/admin/orders")}>
                                View Orders
                            </Button>
                        }
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <StatCard
                        title="Total Revenue"
                        value={formatPrice(systemStats.totalRevenue)}
                        icon={<AttachMoney sx={{ fontSize: 40 }} />}
                        color="success"
                        trend="+8% this month"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <StatCard title="Active Users" value={systemStats.activeUsers} icon={<People sx={{ fontSize: 40 }} />} color="warning" />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <StatCard
                        title="Pending Orders"
                        value={systemStats.pendingOrders}
                        icon={<Warning sx={{ fontSize: 40 }} />}
                        color="error"
                        action={
                            <Button variant="outlined" size="small" color="error">
                                Review Now
                            </Button>
                        }
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={4}>
                    <StatCard title="Total Products" value={systemStats.totalProducts} icon={<Inventory sx={{ fontSize: 40 }} />} color="secondary" />
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
                                <Button variant="contained" startIcon={<People />} onClick={() => navigate("/admin/users")}>
                                    User Management
                                </Button>
                                <Button variant="outlined" startIcon={<ShoppingCart />} onClick={() => navigate("/admin/orders")}>
                                    Order Management
                                </Button>
                                <Button variant="outlined" startIcon={<Inventory />}>
                                    Product Overview
                                </Button>
                                <Button variant="outlined" startIcon={<Settings />}>
                                    System Settings
                                </Button>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Content Sections */}
            <Grid container spacing={3}>
                {/* Recent Users */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Recent Users
                            </Typography>
                            <TableContainer>
                                <Table size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Username</TableCell>
                                            <TableCell>Role</TableCell>
                                            <TableCell>Status</TableCell>
                                            <TableCell>Actions</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {users.slice(0, 5).map((user) => (
                                            <TableRow key={user.id}>
                                                <TableCell>
                                                    <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                                        {user.username}
                                                    </Typography>
                                                    <Typography variant="caption" color="text.secondary">
                                                        {user.fullName}
                                                    </Typography>
                                                </TableCell>
                                                <TableCell>
                                                    <Chip label={user.role} size="small" color={getRoleColor(user.role)} />
                                                </TableCell>
                                                <TableCell>
                                                    <Chip
                                                        label={user.isActive ? "Active" : "Inactive"}
                                                        size="small"
                                                        color={user.isActive ? "success" : "default"}
                                                    />
                                                </TableCell>
                                                <TableCell>
                                                    <IconButton size="small">
                                                        <Visibility />
                                                    </IconButton>
                                                    <IconButton size="small">
                                                        <Edit />
                                                    </IconButton>
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                            <Box sx={{ mt: 2, textAlign: "right" }}>
                                <Button variant="outlined" onClick={() => navigate("/admin/users")}>
                                    View All Users
                                </Button>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* System Activities */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Recent Activities
                            </Typography>
                            <List>
                                {recentActivities.map((activity, index) => (
                                    <ListItem key={index} sx={{ px: 0 }}>
                                        <ListItemIcon>
                                            <Box sx={{ color: `${getActivityColor(activity.type)}.main` }}>{activity.icon}</Box>
                                        </ListItemIcon>
                                        <ListItemText
                                            primary={activity.text}
                                            secondary={activity.time}
                                            primaryTypographyProps={{ variant: "body2" }}
                                            secondaryTypographyProps={{ variant: "caption" }}
                                        />
                                    </ListItem>
                                ))}
                            </List>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Critical Orders */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Orders Requiring Attention
                            </Typography>
                            <TableContainer>
                                <Table>
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>Order ID</TableCell>
                                            <TableCell>Customer</TableCell>
                                            <TableCell>Status</TableCell>
                                            <TableCell>Total Amount</TableCell>
                                            <TableCell>Date</TableCell>
                                            <TableCell>Actions</TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {orders
                                            .filter((order) => order.status === "PENDING_APPROVAL")
                                            .map((order) => (
                                                <TableRow key={order.orderID}>
                                                    <TableCell>
                                                        <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                                            {order.orderID}
                                                        </Typography>
                                                    </TableCell>
                                                    <TableCell>
                                                        <Typography variant="body2">{order.customerName}</Typography>
                                                        <Typography variant="caption" color="text.secondary">
                                                            {order.customerPhone}
                                                        </Typography>
                                                    </TableCell>
                                                    <TableCell>
                                                        <Chip label={order.status} color={getStatusColor(order.status)} size="small" />
                                                    </TableCell>
                                                    <TableCell>{formatPrice(order.totalAmount)}</TableCell>
                                                    <TableCell>{new Date(order.orderDate).toLocaleDateString()}</TableCell>
                                                    <TableCell>
                                                        <Button variant="outlined" size="small" color="success" sx={{ mr: 1 }}>
                                                            Approve
                                                        </Button>
                                                        <Button variant="outlined" size="small" color="error">
                                                            Reject
                                                        </Button>
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                            {orders.filter((order) => order.status === "PENDING_APPROVAL").length === 0 && (
                                <Box sx={{ textAlign: "center", py: 4 }}>
                                    <Typography color="text.secondary">No orders requiring approval at the moment.</Typography>
                                </Box>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Container>
    );
};

export default AdminDashboard;

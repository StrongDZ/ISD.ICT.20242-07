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
    Avatar,
    Tooltip,
    Alert,
    Snackbar,
    Pagination,
    InputAdornment,
} from "@mui/material";
import { Add, Edit, Visibility, Delete, Search, FilterList, PersonAdd, People, AdminPanelSettings, Business } from "@mui/icons-material";
import { authService } from "../../services/authService";
import UserDialog from "../../components/Admin/UserDialog";
import LoadingSpinner from "../../components/Common/LoadingSpinner";

const UserManagementPage = () => {
    const [users, setUsers] = useState([]);
    const [filteredUsers, setFilteredUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState("");
    const [roleFilter, setRoleFilter] = useState("");
    const [statusFilter, setStatusFilter] = useState("");
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(10);

    // Dialog states
    const [userDialog, setUserDialog] = useState({ open: false, user: null, mode: "view" });
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    useEffect(() => {
        loadUsers();
    }, []);

    useEffect(() => {
        filterUsers();
    }, [users, searchQuery, roleFilter, statusFilter]);

    const loadUsers = async () => {
        try {
            setLoading(true);
            const mockUsers = authService.getMockUsers();
            setUsers(mockUsers);
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Failed to load users",
                severity: "error",
            });
        } finally {
            setLoading(false);
        }
    };

    const filterUsers = () => {
        let filtered = users;

        if (searchQuery) {
            filtered = filtered.filter(
                (user) =>
                    user.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    user.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    user.email.toLowerCase().includes(searchQuery.toLowerCase())
            );
        }

        if (roleFilter) {
            filtered = filtered.filter((user) => user.role === roleFilter);
        }

        if (statusFilter) {
            filtered = filtered.filter((user) => {
                if (statusFilter === "active") return user.isActive;
                if (statusFilter === "inactive") return !user.isActive;
                return true;
            });
        }

        setFilteredUsers(filtered);
        setCurrentPage(1);
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

    const getRoleIcon = (role) => {
        switch (role) {
            case "ADMINISTRATOR":
                return <AdminPanelSettings />;
            case "PRODUCT_MANAGER":
                return <Business />;
            case "CUSTOMER":
                return <People />;
            default:
                return <People />;
        }
    };

    const getRoleDisplayName = (role) => {
        switch (role) {
            case "ADMINISTRATOR":
                return "Administrator";
            case "PRODUCT_MANAGER":
                return "Product Manager";
            case "CUSTOMER":
                return "Customer";
            default:
                return role;
        }
    };

    const handleView = (user) => {
        setUserDialog({ open: true, user, mode: "view" });
    };

    const handleEdit = (user) => {
        setUserDialog({ open: true, user, mode: "edit" });
    };

    const handleAdd = () => {
        setUserDialog({ open: true, user: null, mode: "add" });
    };

    const handleSave = async (userData, mode) => {
        try {
            if (mode === "add") {
                console.log("Adding user:", userData);
                setSnackbar({
                    open: true,
                    message: "User added successfully",
                    severity: "success",
                });
            } else {
                console.log("Updating user:", userData);
                setSnackbar({
                    open: true,
                    message: "User updated successfully",
                    severity: "success",
                });
            }

            setUserDialog({ open: false, user: null, mode: "view" });
            // In real app, reload users from API
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Failed to save user",
                severity: "error",
            });
        }
    };

    const handleDelete = async (user) => {
        if (window.confirm(`Are you sure you want to delete user "${user.username}"?`)) {
            try {
                console.log("Deleting user:", user.id);
                setSnackbar({
                    open: true,
                    message: "User deleted successfully",
                    severity: "success",
                });
                // In real app, reload users from API
            } catch (error) {
                setSnackbar({
                    open: true,
                    message: "Failed to delete user",
                    severity: "error",
                });
            }
        }
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString("vi-VN");
    };

    // Pagination
    const totalPages = Math.ceil(filteredUsers.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const paginatedUsers = filteredUsers.slice(startIndex, startIndex + itemsPerPage);

    // Statistics
    const userStats = {
        total: users.length,
        administrators: users.filter((u) => u.role === "ADMINISTRATOR").length,
        managers: users.filter((u) => u.role === "PRODUCT_MANAGER").length,
        customers: users.filter((u) => u.role === "CUSTOMER").length,
        active: users.filter((u) => u.isActive).length,
        inactive: users.filter((u) => !u.isActive).length,
    };

    if (loading) {
        return <LoadingSpinner message="Loading users..." />;
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            {/* Header */}
            <Box sx={{ mb: 4 }}>
                <Typography variant="h4" component="h1" gutterBottom>
                    User Management
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Manage system users, roles, and permissions.
                </Typography>
            </Box>

            {/* Statistics Cards */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={6} md={2}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="primary">
                                {userStats.total}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Total Users
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={2}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="error.main">
                                {userStats.administrators}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Administrators
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={2}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="warning.main">
                                {userStats.managers}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Managers
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={2}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="info.main">
                                {userStats.customers}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Customers
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={2}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="success.main">
                                {userStats.active}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Active
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} sm={6} md={2}>
                    <Card>
                        <CardContent sx={{ textAlign: "center" }}>
                            <Typography variant="h4" color="text.secondary">
                                {userStats.inactive}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Inactive
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Controls */}
            <Card sx={{ mb: 3 }}>
                <CardContent>
                    <Grid container spacing={2} alignItems="center">
                        <Grid item xs={12} sm={4}>
                            <TextField
                                fullWidth
                                placeholder="Search users..."
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
                                <InputLabel>Role</InputLabel>
                                <Select
                                    value={roleFilter}
                                    onChange={(e) => setRoleFilter(e.target.value)}
                                    label="Role"
                                    startAdornment={<FilterList />}
                                >
                                    <MenuItem value="">All Roles</MenuItem>
                                    <MenuItem value="ADMINISTRATOR">Administrator</MenuItem>
                                    <MenuItem value="PRODUCT_MANAGER">Product Manager</MenuItem>
                                    <MenuItem value="CUSTOMER">Customer</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={3}>
                            <FormControl fullWidth>
                                <InputLabel>Status</InputLabel>
                                <Select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)} label="Status">
                                    <MenuItem value="">All Status</MenuItem>
                                    <MenuItem value="active">Active</MenuItem>
                                    <MenuItem value="inactive">Inactive</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={2}>
                            <Button variant="contained" startIcon={<PersonAdd />} onClick={handleAdd} fullWidth>
                                Add User
                            </Button>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>

            {/* Results Count */}
            <Box sx={{ mb: 2 }}>
                <Typography variant="body1">
                    {filteredUsers.length} user{filteredUsers.length !== 1 ? "s" : ""} found
                    {currentPage > 1 && ` (Page ${currentPage} of ${totalPages})`}
                </Typography>
            </Box>

            {/* Users Table */}
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>User</TableCell>
                            <TableCell>Role</TableCell>
                            <TableCell>Contact</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Created</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {paginatedUsers.map((user) => (
                            <TableRow key={user.id} hover>
                                <TableCell>
                                    <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                                        <Avatar sx={{ bgcolor: getRoleColor(user.role) + ".main" }}>{user.fullName?.charAt(0).toUpperCase()}</Avatar>
                                        <Box>
                                            <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                                {user.fullName}
                                            </Typography>
                                            <Typography variant="caption" color="text.secondary">
                                                @{user.username}
                                            </Typography>
                                        </Box>
                                    </Box>
                                </TableCell>
                                <TableCell>
                                    <Chip
                                        icon={getRoleIcon(user.role)}
                                        label={getRoleDisplayName(user.role)}
                                        color={getRoleColor(user.role)}
                                        size="small"
                                    />
                                </TableCell>
                                <TableCell>
                                    <Typography variant="body2">{user.email}</Typography>
                                    <Typography variant="caption" color="text.secondary">
                                        {user.phone}
                                    </Typography>
                                </TableCell>
                                <TableCell>
                                    <Chip label={user.isActive ? "Active" : "Inactive"} color={user.isActive ? "success" : "default"} size="small" />
                                </TableCell>
                                <TableCell>
                                    <Typography variant="body2">{formatDate(user.createdAt)}</Typography>
                                </TableCell>
                                <TableCell>
                                    <Box sx={{ display: "flex", gap: 1 }}>
                                        <Tooltip title="View Details">
                                            <IconButton size="small" onClick={() => handleView(user)}>
                                                <Visibility />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Edit User">
                                            <IconButton size="small" onClick={() => handleEdit(user)}>
                                                <Edit />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Delete User">
                                            <IconButton size="small" color="error" onClick={() => handleDelete(user)}>
                                                <Delete />
                                            </IconButton>
                                        </Tooltip>
                                    </Box>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* Pagination */}
            {totalPages > 1 && (
                <Box sx={{ display: "flex", justifyContent: "center", mt: 3 }}>
                    <Pagination count={totalPages} page={currentPage} onChange={(e, page) => setCurrentPage(page)} color="primary" />
                </Box>
            )}

            {/* User Dialog */}
            <UserDialog
                open={userDialog.open}
                onClose={() => setUserDialog({ open: false, user: null, mode: "view" })}
                user={userDialog.user}
                mode={userDialog.mode}
                onSave={handleSave}
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

export default UserManagementPage;

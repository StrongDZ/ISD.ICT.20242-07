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
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
} from "@mui/material";
import { Add, Edit, Visibility, Delete, Search, FilterList, PersonAdd, People, AdminPanelSettings, Business, Lock, LockOpen, Key } from "@mui/icons-material";
import { userService } from "../../services/userService";
import UserDialog from "../../components/Admin/UserDialog";
import LoadingSpinner from "../../components/Common/LoadingSpinner";

const UserManagementPage = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState("");
    const [roleFilter, setRoleFilter] = useState("");
    const [statusFilter, setStatusFilter] = useState("");
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(10);
    const [totalPages, setTotalPages] = useState(1);

    // Dialog states
    const [userDialog, setUserDialog] = useState({ open: false, user: null, mode: "view" });
    const [passwordDialog, setPasswordDialog] = useState({ open: false, userId: null });
    const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });

    useEffect(() => {
        loadUsers();
    }, [currentPage, searchQuery, roleFilter, statusFilter]);

    const loadUsers = async () => {
        try {
            setLoading(true);
            const response = await userService.getAllUsers(searchQuery, currentPage - 1, itemsPerPage);
            setUsers(response.data);
            setTotalPages(Math.ceil(response.total / itemsPerPage));
        } catch (error) {
            setSnackbar({
                open: true,
                message: error.message || "Failed to load users",
                severity: "error",
            });
        } finally {
            setLoading(false);
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
            // Map email to gmail for backend compatibility
            const submitData = { ...userData, gmail: userData.email };
            delete submitData.email;
            if (mode === "add") {
                await userService.createUser(submitData);
                setSnackbar({
                    open: true,
                    message: "User added successfully",
                    severity: "success",
                });
            } else {
                await userService.updateUser(submitData);
                setSnackbar({
                    open: true,
                    message: "User updated successfully",
                    severity: "success",
                });
            }
            setUserDialog({ open: false, user: null, mode: "view" });
            loadUsers();
        } catch (error) {
            setSnackbar({
                open: true,
                message: error.message || "Failed to save user",
                severity: "error",
            });
        }
    };

    const handleDelete = async (user) => {
        if (window.confirm(`Are you sure you want to delete user "${user.username}"?`)) {
            try {
                await userService.deleteUser(user.id);
                setSnackbar({
                    open: true,
                    message: "User deleted successfully",
                    severity: "success",
                });
                loadUsers();
            } catch (error) {
                setSnackbar({
                    open: true,
                    message: error.message || "Failed to delete user",
                    severity: "error",
                });
            }
        }
    };

    const handleDeleteHard = async (user) => {
        if (window.confirm(`Are you sure you want to permanently delete user "${user.userName}"? This action cannot be undone.`)) {
            try {
                await userService.deleteUserHard(user.id);
                setSnackbar({
                    open: true,
                    message: "User permanently deleted successfully",
                    severity: "success",
                });
                loadUsers();
            } catch (error) {
                setSnackbar({
                    open: true,
                    message: error.message || "Failed to delete user",
                    severity: "error",
                });
            }
        }
    };

    const handleBlock = async (user) => {
        if (window.confirm(`Are you sure you want to block user "${user.userName}"?`)) {
            try {
                await userService.blockUser(user.id);
                setSnackbar({
                    open: true,
                    message: "User blocked successfully",
                    severity: "success",
                });
                loadUsers();
            } catch (error) {
                setSnackbar({
                    open: true,
                    message: error.message || "Failed to block user",
                    severity: "error",
                });
            }
        }
    };

    const handleResetPassword = async (userId) => {
        try {
            await userService.changePassword({
                userId,
                newPassword: "defaultPassword123", // You might want to generate this or let admin set it
                isReset: true
            });
            setSnackbar({
                open: true,
                message: "Password reset successfully",
                severity: "success",
            });
            setPasswordDialog({ open: false, userId: null });
        } catch (error) {
            setSnackbar({
                open: true,
                message: error.message || "Failed to reset password",
                severity: "error",
            });
        }
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

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString("vi-VN");
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
                        {users.map((user) => (
                            <TableRow key={user.id} hover>
                                <TableCell>
                                    <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                                        <Avatar sx={{ bgcolor: getRoleColor(user.role) + ".main" }}>{user.userName?.charAt(0).toUpperCase()}</Avatar>
                                        <Box>
                                            <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                                {user.userName}
                                            </Typography>
                                            <Typography variant="caption" color="text.secondary">
                                                @{user.userName}
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
                                    <Typography variant="body2">{user.gmail}</Typography>
                                </TableCell>
                                <TableCell>
                                    <Chip label={user.user_status === 'ACTIVE' ? "Active" : "Blocked"} color={user.user_status === 'ACTIVE' ? "success" : "error"} size="small" />
                                </TableCell>
                                <TableCell>
                                    <Typography variant="body2">{user.createdAt ? formatDate(user.createdAt) : ""}</Typography>
                                </TableCell>
                                <TableCell>
                                    <Box sx={{ display: "flex", gap: 1 }}>
                                        <Tooltip title="View Details">
                                            <IconButton size="small" onClick={async () => {
                                                const detail = await userService.getUserById(user.id);
                                                setUserDialog({ open: true, user: detail.data, mode: "view" });
                                            }}>
                                                <Visibility />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Edit User">
                                            <IconButton size="small" onClick={async () => {
                                                const detail = await userService.getUserById(user.id);
                                                setUserDialog({ open: true, user: detail.data, mode: "edit" });
                                            }}>
                                                <Edit />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Block User">
                                            <IconButton size="small" color="warning" onClick={() => handleBlock(user)}>
                                                <Lock />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Reset Password">
                                            <IconButton size="small" color="info" onClick={() => setPasswordDialog({ open: true, userId: user.id })}>
                                                <Key />
                                            </IconButton>
                                        </Tooltip>
                                        <Tooltip title="Delete User">
                                            <IconButton size="small" color="error" onClick={() => handleDeleteHard(user)}>
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

            {/* Password Reset Dialog */}
            <Dialog open={passwordDialog.open} onClose={() => setPasswordDialog({ open: false, userId: null })}>
                <DialogTitle>Reset User Password</DialogTitle>
                <DialogContent>
                    <Typography>
                        Are you sure you want to reset this user's password? A new password will be generated and sent to the user's email.
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setPasswordDialog({ open: false, userId: null })}>Cancel</Button>
                    <Button onClick={() => handleResetPassword(passwordDialog.userId)} color="primary" variant="contained">
                        Reset Password
                    </Button>
                </DialogActions>
            </Dialog>

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

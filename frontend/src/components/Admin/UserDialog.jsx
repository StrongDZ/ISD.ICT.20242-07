import React, { useState, useEffect } from "react";
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Grid,
    Box,
    Typography,
    Chip,
    FormControlLabel,
    Switch,
    Alert,
} from "@mui/material";
import { Save, Cancel, Person, Email, Phone, LocationOn } from "@mui/icons-material";

const UserDialog = ({ open, onClose, user, mode = "view", onSave }) => {
    const [formData, setFormData] = useState({
        username: "",
        fullName: "",
        email: "",
        phone: "",
        address: "",
        role: "CUSTOMER",
        isActive: true,
        department: "",
        employeeId: "",
    });
    const [errors, setErrors] = useState({});

    useEffect(() => {
        if (user) {
            setFormData({
                username: user.username || "",
                fullName: user.fullName || "",
                email: user.email || "",
                phone: user.phone || "",
                address: user.address || "",
                role: user.role || "CUSTOMER",
                isActive: user.isActive ?? true,
                department: user.department || "",
                employeeId: user.employeeId || "",
            });
        } else if (mode === "add") {
            setFormData({
                username: "",
                fullName: "",
                email: "",
                phone: "",
                address: "",
                role: "CUSTOMER",
                isActive: true,
                department: "",
                employeeId: "",
            });
        }
        setErrors({});
    }, [user, mode, open]);

    const handleFieldChange = (field, value) => {
        setFormData((prev) => ({
            ...prev,
            [field]: value,
        }));

        if (errors[field]) {
            setErrors((prev) => ({
                ...prev,
                [field]: "",
            }));
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!formData.username?.trim()) {
            newErrors.username = "Username is required";
        }

        if (!formData.fullName?.trim()) {
            newErrors.fullName = "Full name is required";
        }

        if (!formData.email?.trim()) {
            newErrors.email = "Email is required";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            newErrors.email = "Please enter a valid email address";
        }

        if (!formData.phone?.trim()) {
            newErrors.phone = "Phone number is required";
        }

        if (!formData.role) {
            newErrors.role = "Role is required";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSave = () => {
        if (validateForm()) {
            onSave(formData, mode);
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

    const getDialogTitle = () => {
        switch (mode) {
            case "add":
                return "Add New User";
            case "edit":
                return "Edit User";
            case "view":
                return "User Details";
            default:
                return "User";
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>
                <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <Typography variant="h6">{getDialogTitle()}</Typography>
                    {user && (
                        <Box sx={{ display: "flex", gap: 1 }}>
                            <Chip label={getRoleDisplayName(formData.role)} color={getRoleColor(formData.role)} size="small" />
                            <Chip label={formData.isActive ? "Active" : "Inactive"} color={formData.isActive ? "success" : "default"} size="small" />
                        </Box>
                    )}
                </Box>
            </DialogTitle>

            <DialogContent>
                <Grid container spacing={3} sx={{ mt: 1 }}>
                    {/* Error Display */}
                    {Object.keys(errors).length > 0 && (
                        <Grid item xs={12}>
                            <Alert severity="error">
                                Please fix the following errors:
                                <ul style={{ marginBottom: 0 }}>
                                    {Object.entries(errors).map(([field, error]) => (
                                        <li key={field}>{error}</li>
                                    ))}
                                </ul>
                            </Alert>
                        </Grid>
                    )}

                    {/* Basic Information */}
                    <Grid item xs={12}>
                        <Typography variant="h6" gutterBottom>
                            <Person sx={{ mr: 1, verticalAlign: "middle" }} />
                            Basic Information
                        </Typography>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Username *"
                            value={formData.username}
                            onChange={(e) => handleFieldChange("username", e.target.value)}
                            error={!!errors.username}
                            helperText={errors.username}
                            disabled={mode === "view" || (mode === "edit" && user)}
                            required
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Full Name *"
                            value={formData.fullName}
                            onChange={(e) => handleFieldChange("fullName", e.target.value)}
                            error={!!errors.fullName}
                            helperText={errors.fullName}
                            disabled={mode === "view"}
                            required
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Email *"
                            type="email"
                            value={formData.email}
                            onChange={(e) => handleFieldChange("email", e.target.value)}
                            error={!!errors.email}
                            helperText={errors.email}
                            disabled={mode === "view"}
                            InputProps={{
                                startAdornment: <Email sx={{ mr: 1, color: "action.active" }} />,
                            }}
                            required
                        />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <TextField
                            fullWidth
                            label="Phone Number *"
                            value={formData.phone}
                            onChange={(e) => handleFieldChange("phone", e.target.value)}
                            error={!!errors.phone}
                            helperText={errors.phone}
                            disabled={mode === "view"}
                            InputProps={{
                                startAdornment: <Phone sx={{ mr: 1, color: "action.active" }} />,
                            }}
                            required
                        />
                    </Grid>

                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Address"
                            multiline
                            rows={2}
                            value={formData.address}
                            onChange={(e) => handleFieldChange("address", e.target.value)}
                            disabled={mode === "view"}
                            InputProps={{
                                startAdornment: <LocationOn sx={{ mr: 1, color: "action.active", alignSelf: "flex-start", mt: 1 }} />,
                            }}
                        />
                    </Grid>

                    {/* Role & Status */}
                    <Grid item xs={12}>
                        <Typography variant="h6" gutterBottom sx={{ mt: 2 }}>
                            Role & Status
                        </Typography>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <FormControl fullWidth error={!!errors.role}>
                            <InputLabel>Role *</InputLabel>
                            <Select
                                value={formData.role}
                                onChange={(e) => handleFieldChange("role", e.target.value)}
                                label="Role *"
                                disabled={mode === "view"}
                                required
                            >
                                <MenuItem value="CUSTOMER">Customer</MenuItem>
                                <MenuItem value="PRODUCT_MANAGER">Product Manager</MenuItem>
                                <MenuItem value="ADMINISTRATOR">Administrator</MenuItem>
                            </Select>
                        </FormControl>
                    </Grid>

                    <Grid item xs={12} sm={6}>
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={formData.isActive}
                                    onChange={(e) => handleFieldChange("isActive", e.target.checked)}
                                    disabled={mode === "view"}
                                />
                            }
                            label="Active User"
                        />
                    </Grid>

                    {/* Staff Information (for non-customers) */}
                    {formData.role !== "CUSTOMER" && (
                        <>
                            <Grid item xs={12}>
                                <Typography variant="h6" gutterBottom sx={{ mt: 2 }}>
                                    Staff Information
                                </Typography>
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    label="Department"
                                    value={formData.department}
                                    onChange={(e) => handleFieldChange("department", e.target.value)}
                                    disabled={mode === "view"}
                                />
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    label="Employee ID"
                                    value={formData.employeeId}
                                    onChange={(e) => handleFieldChange("employeeId", e.target.value)}
                                    disabled={mode === "view"}
                                />
                            </Grid>
                        </>
                    )}

                    {/* User Statistics (for view mode) */}
                    {mode === "view" && user && (
                        <Grid item xs={12}>
                            <Typography variant="h6" gutterBottom sx={{ mt: 2 }}>
                                User Statistics
                            </Typography>
                            <Box sx={{ p: 2, bgcolor: "grey.50", borderRadius: 1 }}>
                                <Typography variant="body2">
                                    <strong>Created:</strong> {new Date(user.createdAt).toLocaleDateString("vi-VN")}
                                </Typography>
                                {user.orderHistory && (
                                    <Typography variant="body2">
                                        <strong>Total Orders:</strong> {user.orderHistory.length}
                                    </Typography>
                                )}
                                <Typography variant="body2">
                                    <strong>User ID:</strong> {user.id}
                                </Typography>
                            </Box>
                        </Grid>
                    )}
                </Grid>
            </DialogContent>

            <DialogActions>
                <Button onClick={onClose} startIcon={<Cancel />}>
                    {mode === "view" ? "Close" : "Cancel"}
                </Button>

                {mode !== "view" && (
                    <Button onClick={handleSave} variant="contained" startIcon={<Save />}>
                        {mode === "add" ? "Add User" : "Save Changes"}
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
};

export default UserDialog;

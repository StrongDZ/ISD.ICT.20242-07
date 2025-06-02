import React, { useState } from "react";
import { Container, Paper, Box, Typography, TextField, Button, Alert, Link, Divider } from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

const LoginPage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { login } = useAuth();

    const [formData, setFormData] = useState({
        username: "",
        password: "",
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const from = location.state?.from?.pathname || "/";

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError("");

        try {
            const response = await login(formData);

            // Redirect based on user role
            const userRole = response.user.role;

            if (userRole === "ADMINISTRATOR") {
                navigate("/admin", { replace: true });
            } else if (userRole === "PRODUCT_MANAGER") {
                navigate("/manager", { replace: true });
            } else {
                // For customers, go back to the previous page or home
                navigate(from, { replace: true });
            }
        } catch (error) {
            setError(error.message || "Login failed");
        } finally {
            setLoading(false);
        }
    };

    const handleQuickLogin = async (role) => {
        const credentials = {
            CUSTOMER: { username: "customer", password: "customer123" },
            PRODUCT_MANAGER: { username: "manager", password: "manager123" },
            ADMINISTRATOR: { username: "admin", password: "admin123" },
        };

        setFormData(credentials[role]);

        // Auto login after setting credentials
        setLoading(true);
        setError("");

        try {
            const response = await login(credentials[role]);

            // Redirect based on user role
            const userRole = response.user.role;

            if (userRole === "ADMINISTRATOR") {
                navigate("/admin", { replace: true });
            } else if (userRole === "PRODUCT_MANAGER") {
                navigate("/manager", { replace: true });
            } else {
                // For customers, go back to the previous page or home
                navigate(from, { replace: true });
            }
        } catch (error) {
            setError(error.message || "Login failed");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container component="main" maxWidth="sm" sx={{ py: 8 }}>
            <Paper elevation={3} sx={{ p: 4 }}>
                <Box sx={{ textAlign: "center", mb: 4 }}>
                    <Typography component="h1" variant="h4" sx={{ fontWeight: "bold", color: "primary.main" }}>
                        AIMS Login
                    </Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                        Advanced Interactive Media Store
                    </Typography>
                </Box>

                {error && (
                    <Alert severity="error" sx={{ mb: 3 }}>
                        {error}
                    </Alert>
                )}

                <Box component="form" onSubmit={handleSubmit} sx={{ mb: 3 }}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="username"
                        label="Username"
                        name="username"
                        autoComplete="username"
                        autoFocus
                        value={formData.username}
                        onChange={handleInputChange}
                        disabled={loading}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                        value={formData.password}
                        onChange={handleInputChange}
                        disabled={loading}
                    />
                    <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }} disabled={loading}>
                        {loading ? "Signing In..." : "Sign In"}
                    </Button>
                </Box>

                <Divider sx={{ my: 3 }}>
                    <Typography variant="body2" color="text.secondary">
                        Quick Login (Demo)
                    </Typography>
                </Divider>

                <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                    <Button variant="outlined" onClick={() => handleQuickLogin("CUSTOMER")} disabled={loading} fullWidth>
                        Login as Customer
                    </Button>
                    <Button variant="outlined" onClick={() => handleQuickLogin("PRODUCT_MANAGER")} disabled={loading} fullWidth>
                        Login as Product Manager
                    </Button>
                    <Button variant="outlined" onClick={() => handleQuickLogin("ADMINISTRATOR")} disabled={loading} fullWidth>
                        Login as Administrator
                    </Button>
                </Box>

                <Box sx={{ mt: 3, textAlign: "center" }}>
                    <Typography variant="body2" color="text.secondary">
                        Don't have an account?{" "}
                        <Link href="#" underline="hover">
                            Contact Administrator
                        </Link>
                    </Typography>
                </Box>

                <Box sx={{ mt: 4, p: 2, backgroundColor: "grey.50", borderRadius: 1 }}>
                    <Typography variant="h6" gutterBottom>
                        Demo Credentials:
                    </Typography>
                    <Typography variant="body2" sx={{ mb: 1 }}>
                        <strong>Customer:</strong> customer / customer123 → Go to previous page or home
                    </Typography>
                    <Typography variant="body2" sx={{ mb: 1 }}>
                        <strong>Manager:</strong> manager / manager123 → Go to Manager Dashboard
                    </Typography>
                    <Typography variant="body2">
                        <strong>Admin:</strong> admin / admin123 → Go to Admin Dashboard
                    </Typography>
                </Box>
            </Paper>
        </Container>
    );
};

export default LoginPage;

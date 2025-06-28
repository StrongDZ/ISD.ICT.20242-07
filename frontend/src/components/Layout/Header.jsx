import React, { useState } from "react";
import { AppBar, Toolbar, Typography, IconButton, Badge, Menu, MenuItem, Box, Button, TextField, InputAdornment } from "@mui/material";
import { ShoppingCart, Search, AccountCircle, Store } from "@mui/icons-material";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { useCart } from "../../contexts/CartContext";
const Header = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { user, logout, isAuthenticated, isAdmin, isManager, isCustomer } = useAuth();
    const { getCartCount } = useCart();
    const [anchorEl, setAnchorEl] = useState(null);
    const [searchQuery, setSearchQuery] = useState("");

    const handleMenuOpen = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleMenuClose = () => {
        setAnchorEl(null);
    };

    const handleLogout = () => {
        logout();
        handleMenuClose();
        navigate("/");
    };

    const handleSearch = (e) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            navigate(`/products?search=${encodeURIComponent(searchQuery.trim())}`);
        }
    };

    const handleCartClick = () => {
        navigate("/cart");
    };

    const handleLogoClick = () => {
        navigate("/");
    };

    const isActive = (path) => location.pathname === path;

    return (
        <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
            <Toolbar>
                {/* Logo and Title */}

                <Box sx={{ display: "flex", alignItems: "center", cursor: "pointer", mr: 3 }} onClick={handleLogoClick}>
                    <Store sx={{ mr: 1 }} />
                    <Typography variant="h6" component="div" sx={{ fontWeight: "bold" }}>
                        AIMS
                    </Typography>
                </Box>

                {/* Navigation Links */}

                <Box sx={{ display: "flex", gap: 2, mr: "auto" }}>
                    {/* Basic navigation for all users */}
                    {(isCustomer() || !isAuthenticated()) && (
                        <>
                            <Button
                                color="inherit"
                                onClick={() => navigate("/")}
                                sx={{
                                    fontWeight: isActive("/") ? "bold" : "normal",
                                    textDecoration: isActive("/") ? "underline" : "none",
                                }}
                            >
                                Home
                            </Button>
                            <Button
                                color="inherit"
                                onClick={() => navigate("/products")}
                                sx={{
                                    fontWeight: isActive("/products") ? "bold" : "normal",
                                    textDecoration: isActive("/products") ? "underline" : "none",
                                }}
                            >
                                Products
                            </Button>
                        </>
                    )}

                    {/* Admin Links - Only for Admin */}
                    {isAdmin() && (
                        <>
                            <Button
                                color="inherit"
                                onClick={() => navigate("/admin")}
                                sx={{
                                    fontWeight: isActive("/admin") ? "bold" : "normal",
                                    textDecoration: isActive("/admin") ? "underline" : "none",
                                }}
                            >
                                Admin Panel
                            </Button>

                            <Button
                                color="inherit"
                                onClick={() => navigate("/admin/users")}
                                sx={{
                                    fontWeight: isActive("/admin/users") ? "bold" : "normal",
                                    textDecoration: isActive("/admin/users") ? "underline" : "none",
                                }}
                            >
                                User Management
                            </Button>
                        </>
                    )}

                    {/* Manager Links - For Admin and Manager */}
                    {isManager() && (
                        <>
                            <Button
                                color="inherit"
                                onClick={() => navigate("/manager")}
                                sx={{
                                    fontWeight: isActive("/manager") ? "bold" : "normal",
                                    textDecoration: isActive("/manager") ? "underline" : "none",
                                }}
                            >
                                Dashboard
                            </Button>

                            <Button
                                color="inherit"
                                onClick={() => navigate("/manager/products")}
                                sx={{
                                    fontWeight: isActive("/manager/products") ? "bold" : "normal",
                                    textDecoration: isActive("/manager/products") ? "underline" : "none",
                                }}
                            >
                                Product Management
                            </Button>

                            <Button
                                color="inherit"
                                onClick={() => navigate("/manager/orders")}
                                sx={{
                                    fontWeight: isActive("/manager/orders") ? "bold" : "normal",
                                    textDecoration: isActive("/manager/orders") ? "underline" : "none",
                                }}
                            >
                                Order Management
                            </Button>
                        </>
                    )}
                </Box>

                {/* Search Bar - Only for customers and basic navigation */}
                {(isCustomer() || !isAuthenticated()) && (
                    <Box component="form" onSubmit={handleSearch} sx={{ mr: 2, minWidth: "300px" }}>
                        <TextField
                            size="small"
                            placeholder="Search products..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            InputProps={{
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton type="submit" size="small">
                                            <Search />
                                        </IconButton>
                                    </InputAdornment>
                                ),
                                sx: { backgroundColor: "rgba(255, 255, 255, 0.1)", color: "white" },
                            }}
                            sx={{
                                "& .MuiOutlinedInput-root": {
                                    "& fieldset": {
                                        borderColor: "rgba(255, 255, 255, 0.3)",
                                    },
                                    "&:hover fieldset": {
                                        borderColor: "rgba(255, 255, 255, 0.5)",
                                    },
                                    "&.Mui-focused fieldset": {
                                        borderColor: "white",
                                    },
                                },
                                "& .MuiInputBase-input::placeholder": {
                                    color: "rgba(255, 255, 255, 0.7)",
                                    opacity: 1,
                                },
                            }}
                        />
                    </Box>
                )}

                {/* Cart Icon - Only for customers */}
                {(isCustomer() || !isAuthenticated()) && (
                    <IconButton color="inherit" onClick={handleCartClick} sx={{ mr: 1 }}>
                        <Badge badgeContent={getCartCount()} color="secondary">
                            <ShoppingCart />
                        </Badge>
                    </IconButton>
                )}

                {/* User Menu */}
                {isAuthenticated() ? (
                    <>
                        <IconButton color="inherit" onClick={handleMenuOpen}>
                            <AccountCircle />
                        </IconButton>
                        <Menu
                            anchorEl={anchorEl}
                            open={Boolean(anchorEl)}
                            onClose={handleMenuClose}
                            anchorOrigin={{
                                vertical: "bottom",
                                horizontal: "right",
                            }}
                            transformOrigin={{
                                vertical: "top",
                                horizontal: "right",
                            }}
                        >
                            <MenuItem onClick={handleMenuClose}>
                                <Box>
                                    <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                                        {user?.fullName || user?.username}
                                    </Typography>
                                    <Typography variant="caption" color="text.secondary">
                                        {user?.role === "ADMINISTRATOR"
                                            ? "Administrator"
                                            : user?.role === "PRODUCT_MANAGER"
                                            ? "Product Manager"
                                            : "Customer"}
                                    </Typography>
                                </Box>
                            </MenuItem>

                            {/* Role-specific menu items */}
                            {isAdmin() && (
                                <>
                                    <MenuItem
                                        onClick={() => {
                                            navigate("/admin");
                                            handleMenuClose();
                                        }}
                                    >
                                        Admin Dashboard
                                    </MenuItem>
                                    <MenuItem
                                        onClick={() => {
                                            navigate("/admin/users");
                                            handleMenuClose();
                                        }}
                                    >
                                        User Management
                                    </MenuItem>
                                </>
                            )}

                            {isManager() && (
                                <>
                                    <MenuItem
                                        onClick={() => {
                                            navigate("/manager");
                                            handleMenuClose();
                                        }}
                                    >
                                        Dashboard
                                    </MenuItem>
                                    <MenuItem
                                        onClick={() => {
                                            navigate("/manager/products");
                                            handleMenuClose();
                                        }}
                                    >
                                        Product Management
                                    </MenuItem>
                                    <MenuItem
                                        onClick={() => {
                                            navigate("/manager/orders");
                                            handleMenuClose();
                                        }}
                                    >
                                        Order Management
                                    </MenuItem>
                                </>
                            )}

                            {isCustomer() && (
                                <>
                                    <MenuItem
                                        onClick={() => {
                                            navigate("/cart");
                                            handleMenuClose();
                                        }}
                                    >
                                        My Cart ({getCartCount()})
                                    </MenuItem>
                                    <MenuItem
                                        onClick={() => {
                                            navigate("/orders");
                                            handleMenuClose();
                                        }}
                                    >
                                        My Orders
                                    </MenuItem>
                                </>
                            )}

                            <MenuItem onClick={handleLogout}>Logout</MenuItem>
                        </Menu>
                    </>
                ) : (
                    <Button color="inherit" onClick={() => navigate("/login")} sx={{ ml: 1 }}>
                        Login
                    </Button>
                )}
            </Toolbar>
        </AppBar>
    );
};

export default Header;

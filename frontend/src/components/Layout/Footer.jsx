import React from "react";
import { Box, Typography, Container, Link } from "@mui/material";

const Footer = () => {
    return (
        <Box
            component="footer"
            sx={{
                backgroundColor: (theme) => theme.palette.grey[800],
                color: "white",
                py: 3,
                mt: "auto",
            }}
        >
            <Container maxWidth="lg">
                <Box
                    sx={{
                        display: "flex",
                        flexDirection: { xs: "column", md: "row" },
                        justifyContent: "space-between",
                        alignItems: "center",
                        gap: 2,
                    }}
                >
                    <Typography variant="body2">© 2024 AIMS - Advanced Interactive Media Store. All rights reserved.</Typography>

                    <Box sx={{ display: "flex", gap: 3 }}>
                        <Link href="#" color="inherit" underline="hover">
                            Privacy Policy
                        </Link>
                        <Link href="#" color="inherit" underline="hover">
                            Terms of Service
                        </Link>
                        <Link href="#" color="inherit" underline="hover">
                            Contact Us
                        </Link>
                    </Box>
                </Box>
            </Container>
        </Box>
    );
};

export default Footer;

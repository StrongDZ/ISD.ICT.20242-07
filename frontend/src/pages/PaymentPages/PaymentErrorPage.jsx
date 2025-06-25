import React from "react";
import { Container, Typography, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

const PaymentError = () => {
    const navigate = useNavigate();

    return (
        <Container sx={{ mt: 8, textAlign: "center" }}>
            <Typography variant="h4" color="error" gutterBottom>
                ❌ Giao dịch không thành công!
            </Typography>
            <Typography variant="body1" sx={{ mb: 4 }}>
                Có lỗi xảy ra trong quá trình thanh toán. Vui lòng thử lại hoặc liên hệ với bộ phận hỗ trợ.
            </Typography>
            <Button variant="contained" color="primary" onClick={() => navigate("/")}>
                Back to Home
            </Button>
        </Container>
    );
};

export default PaymentError;

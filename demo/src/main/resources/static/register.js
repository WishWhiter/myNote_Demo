document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.querySelector('form[th\:action="@{/register}"]');

    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            // 验证表单
            if (!username || !password || !confirmPassword) {
                alert('请填写所有字段');
                return;
            }

            if (password !== confirmPassword) {
                alert('两次输入的密码不一致');
                return;
            }

            if (password.length < 6) {
                alert('密码长度至少6位');
                return;
            }

            // 创建表单数据
            const formData = new FormData();
            formData.append('username', username);
            formData.append('password', password);

            // 提交表单
            fetch('/register', {
                method: 'POST',
                body: new URLSearchParams({
                    'username': username,
                    'password': password
                })
            })
            .then(response => {
                if (response.redirected) {
                    if (response.url.includes('login')) {
                        alert('注册成功！正在跳转到登录页面...');
                        window.location.href = '/login?registered=true';
                    } else if (response.url.includes('error=userexists')) {
                        alert('用户名已存在，请选择其他用户名');
                    }
                }
                return response.text();
            })
            .catch(error => {
                console.error('注册请求出错:', error);
                alert('注册过程中发生错误，请稍后再试');
            });
        });
    }
});
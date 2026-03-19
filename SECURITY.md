# Security Policy

## Supported Versions

We take security seriously and actively maintain the following versions:

| Version | Supported          |
| ------- | ------------------ |
| Latest  | :white_check_mark: |
| < Latest| :x:                |

We recommend always using the latest version to ensure you have the most recent security updates.

## Reporting a Vulnerability

If you discover a security vulnerability in MyFinance, please follow these steps:

### 1. Do Not Open a Public Issue

**Please do not report security vulnerabilities through public GitHub issues.**

Security vulnerabilities should be disclosed responsibly to give maintainers time to fix them before they become public knowledge.

### 2. Report Privately

Send details of the vulnerability to:

- **Email**: [Create a security advisory on GitHub](https://github.com/your-username/myFinance/security/advisories/new)

Or open a private security advisory on GitHub:
1. Go to the repository's "Security" tab
2. Click "Advisories"
3. Click "New draft security advisory"
4. Fill in the details

### 3. Information to Include

Please provide as much of the following information as possible:

- **Type of vulnerability** (e.g., SQL injection, XSS, authentication bypass)
- **Location** (file path and line number if applicable)
- **Step-by-step reproduction** instructions
- **Proof of concept** or exploit code (if available)
- **Impact assessment** - what can an attacker achieve?
- **Suggested fix** (if you have one)
- **Your contact information** for follow-up questions

### 4. Response Timeline

- **Acknowledgment**: Within 48 hours
- **Initial assessment**: Within 5 business days
- **Fix development**: Depends on severity and complexity
- **Public disclosure**: After fix is released (coordinated disclosure)

## Security Best Practices for Users

### Environment Configuration

1. **Never commit `.env` files**
   ```bash
   # Already in .gitignore
   .env
   .env.local
   .env.*.local
   ```

2. **Use strong passwords**
   - Avoid default passwords in production
   - Use complex, unique passwords
   - Consider using a password manager

3. **Secure your API keys**
   - Keep OpenAI API keys confidential
   - Rotate keys periodically
   - Set usage limits in OpenAI dashboard
   - Monitor for unusual activity

4. **Set proper CORS origins**
   ```env
   # Development
   CORS_ALLOWED_ORIGINS=http://localhost:4200
   
   # Production
   CORS_ALLOWED_ORIGINS=https://your-domain.com
   ```

### Database Security

1. **Change default credentials**
   ```env
   POSTGRES_USER=myfinance_user
   POSTGRES_PASSWORD=strong_random_password_here
   ```

2. **Use secure connections**
   - Enable SSL/TLS for database connections in production
   - Restrict database access to application servers only
   - Use VPC or private networks

3. **Regular backups**
   - Backup your database regularly
   - Test backup restoration procedures
   - Store backups securely

### Application Security

1. **Keep dependencies updated**
   ```bash
   # Backend
   cd api
   mvn versions:display-dependency-updates
   
   # Frontend
   cd web
   npm audit
   npm update
   ```

2. **Use HTTPS in production**
   - Never transmit credentials over HTTP
   - Use valid SSL/TLS certificates
   - Enable HSTS headers

3. **Environment-specific settings**
   - Use different credentials for dev/staging/production
   - Disable debug mode in production
   - Set appropriate logging levels

### Docker Security

1. **Don't expose unnecessary ports**
   ```yaml
   ports:
     - "127.0.0.1:5433:5432"  # Only localhost access
   ```

2. **Use specific image versions**
   ```yaml
   image: pgvector/pgvector:pg17  # Pin to specific version
   ```

3. **Run with non-root user** (when possible)

### API Key Security

1. **OpenAI API Keys**
   - Set spending limits in OpenAI dashboard
   - Monitor usage for anomalies
   - Rotate keys periodically
   - Never log or expose keys in error messages

2. **Key rotation procedure**
   ```bash
   # 1. Generate new key in OpenAI dashboard
   # 2. Update .env file
   # 3. Restart application
   # 4. Revoke old key after confirming new one works
   ```

## Known Security Considerations

### Current Implementation

1. **Authentication**: The application currently uses basic authentication. For production use, consider implementing:
   - JWT tokens with proper expiration
   - OAuth2/OIDC integration
   - Multi-factor authentication
   - Session management with secure cookies

2. **Authorization**: Enhance authorization checks:
   - Implement role-based access control (RBAC)
   - Validate user ownership of resources
   - Add rate limiting to prevent abuse

3. **Input Validation**: Always validate and sanitize user input:
   - Already protected by Spring Boot's validation
   - SQL injection prevented by JPA/Hibernate
   - XSS protection in Angular with DomSanitizer

4. **AI Security**: LangChain4j tools have limited scope:
   - Tools only access user's own data
   - No system commands execution
   - Input sanitization in place

### Recommended Enhancements

For production deployments, consider:

1. **Infrastructure Security**
   - Use a Web Application Firewall (WAF)
   - Implement DDoS protection
   - Set up intrusion detection
   - Use container security scanning

2. **Application Security**
   - Add content security policy (CSP) headers
   - Implement rate limiting
   - Add request signing/verification
   - Enable audit logging

3. **Data Protection**
   - Encrypt sensitive data at rest
   - Use encryption in transit (TLS 1.3+)
   - Implement data retention policies
   - Add PII (Personally Identifiable Information) protection

4. **Monitoring & Alerting**
   - Set up security monitoring
   - Configure alerts for suspicious activity
   - Implement log aggregation and analysis
   - Regular security audits

## Security Checklist for Production

- [ ] Changed all default passwords
- [ ] Using strong, unique credentials
- [ ] API keys secured and rotated
- [ ] HTTPS enabled with valid certificates
- [ ] CORS configured for production domain
- [ ] Database access restricted
- [ ] Connection encryption enabled
- [ ] Debug mode disabled
- [ ] Logging configured (no sensitive data)
- [ ] Backups configured and tested
- [ ] Dependencies up to date
- [ ] Security headers configured
- [ ] Rate limiting implemented
- [ ] Monitoring and alerting set up

## Vulnerability Disclosure Policy

We follow coordinated disclosure:

1. Researcher reports vulnerability privately
2. We acknowledge receipt within 48 hours
3. We assess and develop a fix
4. We release the fix
5. We credit the researcher (with permission)
6. Public disclosure 30 days after fix release

We appreciate responsible disclosure and will credit researchers who follow this process.

## Updates and Patches

Security updates will be released as soon as possible after vulnerabilities are confirmed. Users should:

- Watch the repository for security advisories
- Subscribe to release notifications
- Update promptly when patches are available
- Review security advisories in release notes

## Contact

For security concerns that are not sensitive enough for a security advisory, you can:

- Open a GitHub issue with the "security" label
- Start a discussion in GitHub Discussions

---

Thank you for helping keep MyFinance and its users safe! 🔒

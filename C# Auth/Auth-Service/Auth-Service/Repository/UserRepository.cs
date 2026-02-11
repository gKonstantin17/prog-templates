using Auth_Service.Entity;
using Microsoft.EntityFrameworkCore;

namespace Auth_Service.Repository
{
    public class UserRepository
    {
        private readonly ApplicationContext _db;

        public UserRepository(ApplicationContext context)
        {
            _db = context;
        }

        public async Task<User?> GetByLogin(string login)
        {
            return await _db.User
                .Include(u => u.Role)
                .FirstOrDefaultAsync(u => u.Login == login);
        }

        public async Task<User> Create(User user)
        {
            _db.User.Add(user);
            await _db.SaveChangesAsync();
            return user;
        }

        public async Task<bool> LoginExists(string login)
        {
            return await _db.User.AnyAsync(u => u.Login == login);
        }

        public async Task<User?> GetById(long id)
        {
            return await _db.User
                .Include(u => u.Role)
                .FirstOrDefaultAsync(u => u.Id == id);
        }
    }
}

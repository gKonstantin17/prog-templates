using Domain.DTO;
using Domain.Repositories;
using Domain.Services;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using System.Net.Http;
using System.Text.Json;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace Domain.Controllers
{
    [Route("api/clients")]
    [ApiController]
    public class ClientsController : ControllerBase
    {
        private readonly ClientRepository _clientRepository;
        private readonly ClientService _сlientService;
        public ClientsController(string dalUrl, HttpClientHandler clientHandler)
        {
            _clientRepository = new ClientRepository(dalUrl, clientHandler);
            _сlientService = new ClientService(dalUrl, clientHandler);
        }
        // GET: api/clients
        [HttpGet] //получить список клиентов
        public async Task<ActionResult<List<ClientDto>?>> GetClients()
        {
            var clients = await _clientRepository.Get();
            return clients;
        }
        // GET api/clients/5
        [HttpGet("{id}")] // найти клиента
        public async Task<ActionResult<ClientDto?>> GetClientsById(int id)
        {
            var client = await _clientRepository.GetById(id);
            return client;
        }
        // POST api/clients
        [HttpPost] // регистрация
        public async Task<IActionResult> Post([FromBody] ClientDto request)
        {
            if (await _сlientService.CheckExistClient(request))
                return BadRequest("Пользователь с таким Email существует");

            if (!_сlientService.ValidateClient(request))
                return BadRequest("Неверно введены данные");

            var response = await _clientRepository.Post(request);
            if (response != null)
            {
                return Ok(response);
            }
            else
            {
                return BadRequest();
            }

        }
    }
}
